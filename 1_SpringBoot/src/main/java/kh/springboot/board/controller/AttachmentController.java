package kh.springboot.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
@RequestMapping("/attm")
public class AttachmentController {
    private final BoardService bService;

    @GetMapping("list")
    public String selectList(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model, HttpServletRequest request) {
        int listCount = bService.getListCount(2);
        PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9);
        ArrayList<Board> bList = bService.selectBoardList(pi, 2);
        ArrayList<Attachment> aList = bService.selectAttmBoardList(null);
        if (bList != null) {
            model.addAttribute("bList", bList).addAttribute("aList", aList).addAttribute("pi", pi).addAttribute("loc", request.getRequestURI());
            return "views/attm/list";
        }
        throw new BoardException("첨부파일 게시글 조회를 실패하였습니다.");
    }

    @GetMapping("write")
    public String writeAttm() {
        return "views/attm/write";
    }

    @PostMapping("insert")
    @Transactional
    public String insertAttm(Board b, @RequestParam("file") ArrayList<MultipartFile> files, Model model) {
        String id = ((Member) model.getAttribute("loginUser")).getId();
        b.setBoardWriter(id);

        ArrayList<Attachment> list = new ArrayList<>(); // view에서 넘긴 파일 정보
        for (int i = 0; i < files.size(); i++) {
            MultipartFile upload = files.get(i);
            if (!upload.getOriginalFilename().equals("")) {
                String arr[] = saveFile(upload); // 첨부파일 폴더에 파일 저장
                if (arr[0] != null){
                    Attachment a = new Attachment();
                    a.setOriginalName(upload.getOriginalFilename());
                    a.setRenameName(arr[0]);
                    a.setAttmPath(arr[1]);
                    a.setAttmLevel(1);
                    list.add(a);
                }
            }
        }

        int result1 = 0;
        int result2 = 0;

        if (list.isEmpty()) {
            b.setBoardType(1);
            result1 = bService.insertBoard(b);
        }else{
            b.setBoardType(2);
            list.get(0).setAttmLevel(0);
            result1 = bService.insertBoard(b);
            for (Attachment a : list) {
                a.setRefBoardId(b.getBoardId());
            }
            result2 = bService.insertAttm(list);
        }

        if (result1 + result2 == list.size() + 1) {
            if (result2 == 0) {
                return "redirect:/board/list";
            }else{
                return "redirect:/attm/list";
            }
        }else {
            for (Attachment a : list) {
                deleteFile(a.getRenameName());
            }
            throw new BoardException("첨부파일 게시글 작성을 실패하였습니다.");
        }
    }

    public String[] saveFile(MultipartFile upload) {
        String savePath = "c:\\uploadFiles";
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 같은 폴더에 같은 이름의 파일이 저장되지 않도록 rename -> 년월일시분초밀리랜덤수.확장자
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        int ranNum = (int) (Math.random() * 100000);
        String originalFileName = upload.getOriginalFilename();
        String renameFileName = sdf.format(new Date()) + ranNum + originalFileName.substring(originalFileName.lastIndexOf("."));

        String renamePath = savePath + "\\" + renameFileName;
        try {
            upload.transferTo(new File(renamePath));
        } catch (IllegalStateException | IOException e) {
            System.out.println("파일 전송 에러 : " + e.getMessage());
        }

        String[] returnArr = {renameFileName, savePath};
        return returnArr;
    }

    public void deleteFile(String renameName) {
        String savePath = "c:\\uploadFiles";

        File f = new File(savePath + "\\" + renameName);
        if (f.exists()){
            f.delete();
        }
    }

    @GetMapping("/{id}/{page}")
    public String selectAttm(@PathVariable("page") int page, @PathVariable("id") int bId, Model model) {
        Board b = bService.selectBoard(bId, (Member) model.getAttribute("loginUser"));
        ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
        if (b != null && !list.isEmpty()) {
            model.addAttribute("b", b).addAttribute("page", page).addAttribute("list", list);
            return "views/attm/detail";
        }
        throw new BoardException("첨부파일 게시글 상세보기를 실패하였습니다.");
    }

    @PostMapping("upForm")
    public String updForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
        Board b = bService.selectBoard(bId, null);
        ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
        if (b != null && !list.isEmpty()) {
            model.addAttribute("b", b).addAttribute("page", page).addAttribute("list", list);
        }
        return "views/attm/edit";
    }

    @PostMapping("update")
    @Transactional
    public String updateAttm(Board b, @RequestParam("page") int page, @RequestParam("file") ArrayList<MultipartFile> files, @RequestParam("deleteAttm") String[] deleteAttm, Model model) {
        System.out.println(b);
        System.out.println(Arrays.toString(deleteAttm));
        for (MultipartFile mf : files) {
            System.out.println("fileName : " + mf.getOriginalFilename());
        }

        /*
            1. 새 파일 o
                (1) 기존 파일 모두 삭제 -> 기존 파일 모두 삭제 & 새 파일 저장
                                        새 파일 중에서 level 0, 1 지정
                    Board(boardId=27, boardTitle=wwwww, boardWriter=null, nickName=null, boardContent=aaaaa, boardCount=0, boardCreateDate=null, boardModifyDate=null, boardStatus=null, boardType=0)
                    [2025011410442032763564.png/0, 2025011410442032899353.jpg/1]
                    fileName : 시나모롤.jpg
                (2) 기존 파일 일부 삭제 -> 기존 파일 일부 삭제 & 새 파일 저장
                                        삭제할 파일의 level 검사 후 level이 0인 파일이 삭제되면 다른 기존 파일의 레벨을 0으로 재지정,
                                        새 파일의 레벨은 모두 1로 지정
                    Board(boardId=27, boardTitle=wwwww, boardWriter=null, nickName=null, boardContent=aaaaa, boardCount=0, boardCreateDate=null, boardModifyDate=null, boardStatus=null, boardType=0)
                    [2025011410442032763564.png/0, ]
                    fileName : 시나모롤.jpg
                (3) 기존 파일 모두 유지 -> 새 파일 저장
                                        새 파일의 레벨은 모두 1로 지정
                    Board(boardId=27, boardTitle=wwwww, boardWriter=null, nickName=null, boardContent=aaaaa, boardCount=0, boardCreateDate=null, boardModifyDate=null, boardStatus=null, boardType=0)
                    [, ]
                    fileName : 시나모롤.jpg

            2. 새 파일 x
                (1) 기존 파일 모두 삭제 -> 기존 파일 모두 삭제
                                        일반 게시판으로 이동 : board_type = 1
                    Board(boardId=27, boardTitle=wwwww, boardWriter=null, nickName=null, boardContent=aaaaa, boardCount=0, boardCreateDate=null, boardModifyDate=null, boardStatus=null, boardType=0)
                    [2025011410442032763564.png/0, 2025011410442032899353.jpg/1]
                    fileName :
                (2) 기존 파일 일부 삭제 -> 기존 파일 일부 삭제
                                        삭제할 파일의 level 검사 후 level이 0인 파일이 삭제되면 다른 기존 파일의 레벨을 0으로 재지정
                    Board(boardId=27, boardTitle=wwwww, boardWriter=null, nickName=null, boardContent=aaaaa, boardCount=0, boardCreateDate=null, boardModifyDate=null, boardStatus=null, boardType=0)
                    [2025011410442032763564.png/0, ]
                    fileName :
                (3) 기존 파일 모두 유지 -> board만 수정
                    Board(boardId=27, boardTitle=wwwww, boardWriter=null, nickName=null, boardContent=aaaaa, boardCount=0, boardCreateDate=null, boardModifyDate=null, boardStatus=null, boardType=0)
                    [, ]
                    fileName :

         */

        b.setBoardType(2);

        // 새로 넣는 파일이 있다면 list에 옮겨담기
        ArrayList<Attachment> list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile upload = files.get(i);

            if (!upload.getOriginalFilename().equals("")){
                String arr[] = saveFile(upload);
                if (arr[0] != null){
                    Attachment a = new Attachment();
                    a.setOriginalName(upload.getOriginalFilename());
                    a.setRenameName(arr[0]);
                    a.setAttmPath(arr[1]);
                    a.setAttmLevel(1);
                    a.setRefBoardId(b.getBoardId());
                    list.add(a);
                }
            }
        }

        // 삭제할 파일이 있다면 삭제할 파일의 이름과 레벨을 각각 delRename과 delLevel에 옮겨담기
        ArrayList<String> delRename = new ArrayList<>();
        ArrayList<Integer> delLevel = new ArrayList<>();
        for (String s : deleteAttm) {
            if (!s.equals("")) {
                String[] arr = s.split("/");
                delRename.add(arr[0]);
                delLevel.add(Integer.parseInt(arr[1]));
            }
        }

        int deleteAttmResult = 0;           // 파일 delete 후 결과 값
        int updateBoardResult = 0;           // 게시글 update 후 결과 값
        boolean existBeforeAttm = true;     // 이전 첨부파일이 존재하는지에 대한 여부
        if (!delRename.isEmpty()) { // 저장했던 파일 중 하나라도 삭제하겠다고 한 경우
            deleteAttmResult = bService.deleteAttm(delRename);
            if (deleteAttmResult > 0) {
                for (String s : delRename) {
                    deleteFile(s);
                }
            }

            if (deleteAttm.length != 0 && delRename.size() == deleteAttm.length) { // 기존 파일을 모두 삭제
                existBeforeAttm = false;
                if (list.isEmpty()) {
                    b.setBoardType(1);
                }
            }else{
                for(int level : delLevel){
                    if(level == 0){
                        bService.updateAttmLevel(b.getBoardId());
                        break;
                    }
                }
            }
        }

        if (!existBeforeAttm) {
            if (!list.isEmpty()) {
                list.get(0).setAttmLevel(0);
            }
        }

        updateBoardResult = bService.updateBoard(b);

        int updateAttmResult = 0;
        if (!list.isEmpty()) {
            updateAttmResult = bService.insertAttm(list);
        }

        if (updateBoardResult + updateAttmResult == list.size() + 1) {
            model.addAttribute("b", b).addAttribute("page", page).addAttribute("list", list);
            if (!existBeforeAttm && updateAttmResult == 0) {
                return "redirect:/board/list";
            }
            return String.format("redirect:/attm/%d/%d", b.getBoardId(), page);
        }
        throw new BoardException("첨부파일 게시글 수정을 실패하였습니다.");
    }

//    @PostMapping("delete")
//    @Transactional
//    public String deleteBoard(@RequestParam("boardId") int bId) {
//        int result1 = bService.deleteBoard(bId);
//        int result2 = bService.statusAttm(bId);
//        if (result1 >0 && result2 > 0) {
//            return "redirect:/attm/list";
//        }
//        throw new BoardException("첨부파일 게시글 삭제를 실패하였습니다.");
//    }
}
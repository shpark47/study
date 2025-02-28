package kh.springboot.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
@RequestMapping("/board")
public class BoardController {
    private final BoardService bService;
    private final ClassLoaderTemplateResolver boardResolver;

    @GetMapping("list")
    public String selectList(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model, HttpServletRequest request) {
        int listCount = bService.getListCount(1);

        PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
        ArrayList<Board> list = bService.selectBoardList(pi, 1);
        model.addAttribute("list", list).addAttribute("pi", pi);
        model.addAttribute("loc", request.getRequestURI());
        return "list";
    }

    @GetMapping("write")
    public String writeBoard() {
        return "write";
    }

    @PostMapping("insert")
    public String insertBoard(Board b, Model model) {
        if (b.getBoardTitle().trim().equals("")) {
            throw new BoardException("실패");
        }
        b.setBoardWriter(((Member)model.getAttribute("loginUser")).getId());
        b.setBoardType(1);
        int result = bService.insertBoard(b);
        if (result > 0) {
            return "redirect:/board/list";
        }else{
            throw new BoardException("게시글 작성을 실패하였습니다.");
        }
    }

    @GetMapping("/{id}/{page}")
    public String selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page, Model model) {
        Member m = (Member) model.getAttribute("loginUser");
        Board b = bService.selectBoard(bId, m);
        ArrayList<Reply> list = bService.selectReplyList(bId);
        if (b != null) {
            model.addAttribute("b", b).addAttribute("page", page).addAttribute("list", list);
            return "detail";
        }
        throw new BoardException("게시글 상세조회를 실패하였습니다.");
    }

    @PostMapping("upForm")
    public String upForm(Board b, @RequestParam("page") int page, Model model) {
        // detail.html에서 boardTitle이 네이밍이 안되어 있기 때문에 다시 보드를 select해서 edit.html로 page와 같이 넘겨줌
        model.addAttribute("b", bService.selectBoard(b.getBoardId(), null)).addAttribute("page", page);
        return "views/board/edit";
    }

    @PostMapping("update")
    public String updateBoard(Board b, @RequestParam("page") int page){
        b.setBoardType(1);
        int result = bService.updateBoard(b);
        if (result > 0) {
//            return "redirect:/board/" + b.getBoardId() + "/" + page;
            return String.format("redirect:/board/%d/%d", b.getBoardId(), page);
        }
        throw new BoardException("실패");
    }

    @PostMapping("delete")
    public String deleteBoard(@RequestParam("boardId") int bId, HttpServletRequest request){
        int result = bService.deleteBoard(bId);
        if (result > 0) {
//            return "redirect:/" + (request.getRequestURI().contains("board") ? "board" : "attm") + "/list";
            return "redirect:/" + (request.getHeader("referer").contains("board") ? "board" : "attm") + "/list";
        }
        throw new BoardException("실패");
    }
    // JSON 버전
//    @GetMapping(value="top", produces = "application/json; charset=UTF-8")
//    @ResponseBody
//    public String selectTop(/*HttpServletResponse response*/) {
//        ArrayList<Board> list = bService.selectTop();
//        JSONArray arr = new JSONArray();
//        for (Board b : list) {
//            JSONObject json = new JSONObject();
//            json.put("boardId", b.getBoardId());
//            json.put("boardTitle", b.getBoardTitle());
//            json.put("nickName", b.getNickName());
//            json.put("modifyDate", b.getBoardModifyDate());
//            json.put("boardCount", b.getBoardCount());
//            arr.put(json);
//        }
////        response.setContentType("application/json; charset=utf-8");
//        return arr.toString();
//    }

    // GSON 버전
//    @GetMapping("top")
//    public void selectTop(HttpServletResponse response) {
//        ArrayList<Board> list = bService.selectTop();
//
//        response.setContentType("application/json; charset=utf-8");
//
//        GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
//        Gson gson = gb.create();
//        try {
//            gson.toJson(list, response.getWriter());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @GetMapping(value="rInsert", produces = "application/json; charset=UTF-8")
//    @ResponseBody
//    public String insertReply(Reply r) {
//        int result = bService.insertReply(r);
//        if (result > 0) {
//            ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//            JSONArray arr = new JSONArray();
//            for (Reply reply : list) {
//                JSONObject json = new JSONObject();
//                json.put("nickName", reply.getNickName());
//                json.put("replyContent", reply.getReplyContent());
//                json.put("replyModifyDate", reply.getReplyModifyDate());
//                arr.put(json);
//            }
//            return arr.toString();
//        }
//        throw new BoardException("실패");
//    }

    //Gson
//    @GetMapping(value="rInsert")
//    @ResponseBody
//    public void insertReply(Reply r, HttpServletResponse response) {
//        int result = bService.insertReply(r);
//        if (result > 0) {
//            response.setContentType("application/json; charset=utf-8");
//            ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//            GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
//            Gson gson = gb.create();
//            try {
//                gson.toJson(list, response.getWriter());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //jackson
//    @GetMapping("rInsert")
//    @ResponseBody
//    public String insertReply(Reply r) {
//        int result = bService.insertReply(r);
//        if (result > 0) {
//            ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//            ObjectMapper om = new ObjectMapper();
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            om.setDateFormat(df);
//            try {
//                return om.writeValueAsString(list);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        throw new BoardException("실패");
//    }
//
//    @GetMapping("rDelete")
//    @ResponseBody
//    public int deleteReply(@RequestParam("replyId") int replyId) {
//        return bService.deleteReply(replyId);
//    }
//
//    @GetMapping("rUpdate")
//    @ResponseBody
//    public int updateReply(Reply r) {
//        return bService.updateReply(r);
//    }
}

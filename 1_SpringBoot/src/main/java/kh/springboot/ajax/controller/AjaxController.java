package kh.springboot.ajax.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@RestController // Controller + ResponseBody
@RequestMapping({"/member", "/board", "/admin"})
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class AjaxController {

    private final MemberService mService;

    private final BoardService bService;

    private final JavaMailSender mailSender;

    @GetMapping("checkValue")
    public int checkValue(@RequestParam("value") String value, @RequestParam("col") String col) {
        HashMap<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("col", col);
        return mService.checkValue(map);
    }

    @PutMapping("profile")
    public int updateProfile(@RequestParam(value="profile", required = false) MultipartFile profile, Model model) {
        Member m = (Member) model.getAttribute("loginUser");

        String saveFile = "c:\\profiles";
        File folder = new File(saveFile);
        if (!folder.exists()) folder.mkdirs();

        if (m.getProfile() != null) {
            File f = new File(saveFile + "\\" + m.getProfile());
            f.delete();
        }

        String renameFileName = null;
        if (profile != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            int ranNum = (int) (Math.random() * 100000);
            String originalFilename = profile.getOriginalFilename();
            renameFileName = sdf.format(new Date()) + ranNum + "." + originalFilename.substring(originalFilename.lastIndexOf("."));

            try {
                profile.transferTo(new File(folder + "\\" + renameFileName));
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("id", m.getId());
        map.put("profile", renameFileName);

        int result = mService.updateProfile(map);
        if (result > 0){
            m.setProfile(renameFileName);
            model.addAttribute("loginUser", m);
            return result;
        }
        throw new MemberException("실패");
    }

    @GetMapping("echeck")
    public String checkEmail(@RequestParam("email")String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "[SpringBoot] 이메일 확인";
        String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br>";
        body += "<div style='border: 3px solid green; text-align: center; font-size: 15px;>본 메일은 이메일을 확인하기 위해 발송되었습니다.<br>";
        body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br><br>";

        String random = "";
        for(int i = 0; i < 5; i++) {
            random += (int)(Math.random() * 10);
        }

        body += "<span style='font-size: 30px; text-decoration: underline;'><b>" + random + "</b></span><br></div>";
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);

        return random;
    }

    @PostMapping("list")
    public int insertTodo(TodoList todo){
        return mService.insertTodo(todo);
    }

    @PutMapping("list")
    public int updateTodo(TodoList todo){
        return mService.updateTodo(todo);
    }

    @DeleteMapping("list")
    public int deleteTodo(TodoList todo){
        return mService.deleteTodo(todo.getTodoNum());
    }

    @GetMapping("top")
    public ArrayList<Board> selectTop(HttpServletResponse response) {
        // HttpMessageConverter
        // 기본 문자 : StringHttpMessageConverter
        // 기본 객체 : MappingJackson2HttpMessageConverter -> application/json
        return bService.selectTop();
    }

    @PostMapping("reply")
    public ArrayList<Reply> insertReply(Reply r) {
        int result = bService.insertReply(r);
        if (result > 0) {
            return bService.selectReplyList(r.getRefBoardId());
        }
        throw new BoardException("실패");
    }

    @DeleteMapping("reply")
    public int deleteReply(@RequestParam("replyId") int replyId) {
        return bService.deleteReply(replyId);
    }

    @PutMapping("reply")
    public int updateReply(Reply r) {
        return bService.updateReply(r);
    }

    @PutMapping("members")
    public int updateMember(@RequestParam("val") String value, @RequestParam("col") String column, @RequestParam("id") String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("val", value);
        map.put("col", column);
        map.put("id", id);
        return mService.updateMemberItem(map);
    }
}

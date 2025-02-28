package kh.springboot.member.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
@RequestMapping("/member")
@Slf4j
public class MemberController {

    // 의존성 주입 1. 필드 주입 @Autowired
//    @Autowired
//    private MemberService mService;

    // 의존성 주입 2. 생성자 주입 @RequiredArgsConstructor + final
    //      @RequiredArgsConstructor : 특정 변수(final이 붙은 상수 혹은 @NotNull이 붙은 변수)만 가지고 생성자 생성
    private final MemberService mService;

    private final BCryptPasswordEncoder bcrypt;

    @GetMapping("signIn")
    public String singIn() {
        //로그인 화면 연결
        return "login";
    }

    /* 파라미터 전송받는 방법 */
    // 로그인

    // 1. (Servlet방식) HttpServletRequest 이용
//    @PostMapping("/member/signIn")
//    public void login(HttpServletRequest request) {
//        String id = request.getParameter("id");
//        String pwd = request.getParameter("pwd");
//        System.out.println("id: " + id + " pwd: " + pwd);
//    }

    // 2. @RequestParam 이용
    //      value           view에서 받아올 파라미터 이름(view의 name)이 들어가는 곳
    //                      @RequestParam에 들어가는 속성이 value 하나 뿐이라면 생략 가능
    //      defaultValue    값이 null이거나 들어오지 않았을 때 기본적으로 들어갈 데이터를 지정하는 속성
    //      required        기본 값 true, 지정한 파라미터가 꼭 필요한(필수적인) 변수인지 설정하는 속성
//    @PostMapping("/member/signIn")
//    public void login(@RequestParam(value="id", defaultValue="testId") String id, @RequestParam(value="pwd") String pwd, @RequestParam(value="tt", required=false) String t) {
//        System.out.println("id: " + id + " pwd: " + pwd + " tt: " + t);
//    }

    // 3. @RequestParam 생략
//    @PostMapping("/member/signIn")
//    public void login(@RequestParam("id") String id, @RequestParam("pwd") String pwd) {
//        System.out.println("id: " + id + " pwd: " + pwd);
//    }

    // 4. @ModelAttribute
    //      기본 생성자와 setter를 이용한 주입 방식
//    @PostMapping("/member/signIn")
//    public void login(@ModelAttribute Member m) {
//        System.out.println("id: " + m.getId() + " pwd: " + m.getPwd());
//    }

    // 5. @ModelAttribute 생략
//    @PostMapping("/member/signIn")
//    public String login(Member m, HttpSession session) {
//        Member loginUser = mService.login(m);
//        System.out.println(loginUser);
//        if (loginUser != null) {
//            session.setAttribute("loginUser", loginUser);
//            return "redirect:/home";
//        }else{
//            throw new MemberException("로그인을 실패하였습니다.");
//        }
//    }

    // 안호화 후 로그인
//    @PostMapping("/member/signIn")
//    public String login(Member m, HttpSession session) {
//        Member loginUser = mService.login(m);
//        System.out.println(loginUser);
//        if (loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//            session.setAttribute("loginUser", loginUser);
//            return "redirect:/home";
//        }else{
//            throw new MemberException("로그인을 실패하였습니다.");
//        }
//    }

    // 암호화 후 로그인 + @SessionAttributes
    //      @SessionAttributes는 model에 attribute가 추가될 때 자동으로 키 값을 찾아 세션에 등록하는 어노테이션
    @PostMapping("signIn")
    public String login(Member m, Model model, @RequestParam("beforeURL") String beforeURL) {
        Member loginUser = mService.login(m);
        System.out.println(loginUser);
        if (loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
            model.addAttribute("loginUser", loginUser);
            if (loginUser.getIsAdmin().equals("Y")){
                return "redirect:/admin/home";
            }
//            log.debug(m.getId());
            return "redirect:" + beforeURL;
        }else{
            throw new MemberException("로그인을 실패하였습니다.");
        }
    }

//    @GetMapping("/member/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/home";
//    }

    @GetMapping("logout")
    public String logout(SessionStatus session) {
        session.setComplete();
        return "redirect:/home";
    }

    @GetMapping("enroll")
    public String enroll() {
        // 로그 레벨 : DEBUG < INFO < WARN < ERROR < FATAL
        // fatal    : 아주 심각한 에러
        // error    : 어떤 요청 처리 중 문제 발생
        // warn     : 프로그램 실행에는 문제가 없지만 향후 시스템 에러의 원인이 될 수 있다는 경고성 메세지
        // info     : 정보성 메세지
        // debug    : 디버깅 용도로 사용하는 메세지
        // trace    : 디버그 레벨이 너무 광범위한 것을 해결하기 위해 좀 더 상세한 이벤트를 나타냄

//        log.fatal("회원가입 페이지");
        log.error("회원가입 페이지");
        log.warn("회원가입 페이지");
        log.info("회원가입 페이지");
        log.debug("회원가입 페이지");
        log.trace("회원가입 페이지");
        return "enroll";
    }

    @PostMapping("enroll")
    public String enroll(Member m, @RequestParam("emailId") String emailId, @RequestParam("emailDomain") String emailDomain) {
        if (!emailId.trim().isEmpty()){
            m.setEmail(emailId + "@" + emailDomain);
        }
        m.setPwd(bcrypt.encode(m.getPwd()));
        int result = mService.insertMember(m);
        if (result > 0){
            return "redirect:/home";
        }else{
            throw new MemberException("회원가입을 실패하였습니다.");
        }
    }

    // 1. Model
    //      앱 형식으로 request scope에 데이터를 담아 전달
//    @GetMapping("/member/myInfo")
//    public String myInfo(HttpSession session, Model model) {
//        Member loginUser = (Member) session.getAttribute("loginUser");
//        if (loginUser != null) {
//            String id = loginUser.getId();
//            ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
//            model.addAttribute("list", list);
//        }
//        return "views/member/myInfo";
//    }

    // 2. ModelAndView 이용
    //      Model + View
    @GetMapping("myInfo")
    public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser != null) {
            String id = loginUser.getId();
            ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);

            ArrayList<TodoList> todoList = mService.selectTodoList(id);

            mv.addObject("list", list).addObject("todoList", todoList);
            mv.setViewName("myInfo");
        }
        return mv;
    }

    @GetMapping("edit")
    public String edit() {
        return "edit";
    }

    @PostMapping("edit")
    public String edit(Member m, @RequestParam("emailId") String emailId, @RequestParam("emailDomain") String emailDomain, Model model) {
        if (!emailId.trim().isEmpty()){
            m.setEmail(emailId + "@" + emailDomain);
        }
        int result = mService.updateMember(m);
        if (result > 0){
            model.addAttribute("loginUser", mService.login(m));
            return "redirect:/member/myInfo";
        }else {
            throw new MemberException("회원정보 수정을 실패했습니다.");
        }
    }

    @PostMapping("updatePassword")
    public String updatePassword(@RequestParam("currentPwd") String currentPwd, @RequestParam("newPwd") String newPwd, Model model) {
        Member m = (Member) model.getAttribute("loginUser");
        if (bcrypt.matches(currentPwd, m.getPwd())) {
            m.setPwd(bcrypt.encode(newPwd));
            int result = mService.updatePwd(m);
            if (result > 0){
                return "redirect:/member/logout";
            }
        }
        throw new MemberException("실패");
    }

    @GetMapping("delete")
    public String delete(Model model) {
        String id = ((Member) model.getAttribute("loginUser")).getId();
        int result = mService.deleteMember(id);
        if (result > 0){
            return "redirect:/member/logout";
        }
        throw new MemberException("실패");
    }

//    @GetMapping("checkId")
//    public void checkId(@RequestParam("id") String id, PrintWriter out) {
//        int count = mService.checkId(id);
//        out.print(count);
//    }
//
//    @GetMapping("checkNickName")
//    @ResponseBody
//    public String checkNickName(@RequestParam("nickName") String nickName) {
//        int count = mService.checkNickName(nickName);
//        return count == 0 ? "usable" : "unusable";
//    }

//    @GetMapping("checkValue")
//    @ResponseBody
//    public int checkValue(@RequestParam("value") String value, @RequestParam("col") String col) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("value", value);
//        map.put("col", col);
//        return mService.checkValue(map);
//    }

//    @PostMapping("profile")
//    @ResponseBody
//    public int updateProfile(@RequestParam(value="profile", required = false) MultipartFile profile, Model model) {
//        Member m = (Member) model.getAttribute("loginUser");
//
//        String saveFile = "c:\\profiles";
//        File folder = new File(saveFile);
//        if (!folder.exists()) folder.mkdirs();
//
//        if (m.getProfile() != null) {
//            File f = new File(saveFile + "\\" + m.getProfile());
//            f.delete();
//        }
//
//        String renameFileName = null;
//        if (profile != null) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//            int ranNum = (int) (Math.random() * 100000);
//            String originalFilename = profile.getOriginalFilename();
//            renameFileName = sdf.format(new Date()) + ranNum + "." + originalFilename.substring(originalFilename.lastIndexOf("."));
//
//            try {
//                profile.transferTo(new File(folder + "\\" + renameFileName));
//            } catch (IllegalStateException | IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("id", m.getId());
//        map.put("profile", renameFileName);
//
//        int result = mService.updateProfile(map);
//        if (result > 0){
//            m.setProfile(renameFileName);
//            model.addAttribute("loginUser", m);
//            return result;
//        }
//        throw new MemberException("실패");
//    }

//    @GetMapping("echeck")
//    @ResponseBody
//    public String checkEmail(@RequestParam("email")String email) {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//        String subject = "[SpringBoot] 이메일 확인";
//        String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br>";
//        body += "<div style='border: 3px solid green; text-align: center; font-size: 15px;>본 메일은 이메일을 확인하기 위해 발송되었습니다.<br>";
//        body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br><br>";
//
//        String random = "";
//        for(int i = 0; i < 5; i++) {
//            random += (int)(Math.random() * 10);
//        }
//
//        body += "<span style='font-size: 30px; text-decoration: underline;'><b>" + random + "</b></span><br></div>";
//        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
//        try {
//            mimeMessageHelper.setTo(email);
//            mimeMessageHelper.setSubject(subject);
//            mimeMessageHelper.setText(body, true);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        mailSender.send(mimeMessage);
//
//        return random;
//    }

    @GetMapping("findIDPW")
    public String findIDPW() {
        return "findIDPW";
    }

//    @PostMapping("fid")
//    public String findId(Member m, Model model) {
//        String id = mService.findId(m);
//        if (id != null) {
//            model.addAttribute("id", id);
//            return "findId";
//        }
//        throw new MemberException("존재하지 않는 회원입니다.");
//    }

//    @PostMapping("fpw")
//    public String findPw(Member m, Model model) {
//        Member member = mService.findPw(m);
//        if (member != null) {
//            return "resetPw";
//        }
//        throw new MemberException("존재하지 않는 회원입니다.");
//    }

    @PostMapping("fInfo")
    public String findId(Member m, Model model) {
        Member member = mService.findInfo(m);
        if (member != null) {
            model.addAttribute("id", member.getId());
            return m.getName() != null ? "findId" : "resetPw";
        }
        throw new MemberException("존재하지 않는 회원입니다.");
    }

    @PostMapping("fpwUpdate")
    public String updatePwd(Member m, Model model) {
        m.setPwd(bcrypt.encode(m.getPwd()));
        int result = mService.updatePwd(m);
        if (result > 0){
            model.addAttribute("msg", "비밀번호 수정이 완료되었습니다.");
            model.addAttribute("url", "/home");
            return "views/common/sendRedirect";
        }
        throw new MemberException("비밀번호 수정 실패");
    }

//    @GetMapping("linsert")
//    @ResponseBody
//    public int insertTodo(TodoList todo){
//        return mService.insertTodo(todo);
//    }
//
//    @GetMapping("lupdate")
//    @ResponseBody
//    public int updateTodo(TodoList todo){
//        return mService.updateTodo(todo);
//    }
//
//    @GetMapping("ldelete")
//    @ResponseBody
//    public int deleteTodo(TodoList todo){
//        return mService.deleteTodo(todo.getTodoNum());
//    }
}

package kh.springboot.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.vo.Member;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

// 로그인이 되어있는지 검사
public class CheckLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            String url = request.getRequestURI();
            String msg = null;
            String regExp = "/(board|attm)/\\d+/\\d+";
            if (Pattern.matches(regExp, url)) {
                msg = "로그인 후 이용하세요.";
            }else{
                msg = "로그인 세션이 만료되어 로그인 화면으로 넘어가";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("<script>alert('"+msg+"'); location.href='/member/signIn';</script>");
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

package kh.springboot.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.vo.Member;
import org.springframework.web.servlet.HandlerInterceptor;

public class CheckAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getIsAdmin().equals("N")) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("<script>alert('접근 권한이 없습니다.'); location.href='/home';</script>");
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

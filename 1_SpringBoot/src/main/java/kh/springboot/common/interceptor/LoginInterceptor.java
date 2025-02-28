package kh.springboot.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.springboot.member.model.vo.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Member m = (Member) request.getSession().getAttribute("loginUser");
        if (m != null && m.getIsAdmin().equals("N")) {
            log.debug(m.getId());
        }
    }
}

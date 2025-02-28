package kh.springboot.common.config;

import kh.springboot.common.interceptor.CheckAdminInterceptor;
import kh.springboot.common.interceptor.CheckLoginInterceptor;
import kh.springboot.common.interceptor.LoginInterceptor;
import kh.springboot.common.interceptor.TestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // 매핑 uri 설정 (파일을 가지고 올 때 경로를 어떻게 쓸지 결정)
                .addResourceLocations("file:///c:/uploadFiles/", "classpath:/static/", "file:///c:/profiles/"); // 정적 리소스 위치
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new TestInterceptor()) // 인터셉터 등록
//                .addPathPatterns("/**"); // 인터셉터가 가로챌 url 등록

        registry.addInterceptor(new CheckLoginInterceptor())
                .addPathPatterns("/member/myInfo", "/member/edit", "/member/delete", "/member/updatePassword")
                .addPathPatterns("/board/**", "/attm/**")
                .excludePathPatterns("/board/list", "/board/top", "/attm/list");

        registry.addInterceptor(new CheckAdminInterceptor())
                .addPathPatterns("/admin/**");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/member/signIn");
    }
}

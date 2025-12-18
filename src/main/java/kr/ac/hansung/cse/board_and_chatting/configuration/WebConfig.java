package kr.ac.hansung.cse.board_and_chatting.configuration;

import kr.ac.hansung.cse.board_and_chatting.configuration.resolver.RequestParameterResolver;
import kr.ac.hansung.cse.board_and_chatting.configuration.resolver.RequestParser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestParser requestParser;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 URL 허용
                .allowedOrigins("http://localhost:5173", "http://localhost:5174") // React 개발 서버
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true); // 세션 쿠키 허용
    }


    // Resolver 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestParameterResolver(requestParser));
    }
}
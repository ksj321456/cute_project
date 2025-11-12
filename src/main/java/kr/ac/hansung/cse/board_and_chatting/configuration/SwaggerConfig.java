package kr.ac.hansung.cse.board_and_chatting.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Board and Chatting API")
                        .description("게시판 및 채팅 API 문서")
                        .version("1.0.0"));
    }
}
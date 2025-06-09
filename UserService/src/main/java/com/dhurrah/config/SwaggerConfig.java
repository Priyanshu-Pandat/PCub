package com.dhurrah.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                        .info(new Info()
                        .title("PCUB API")
                        .version("1.0").description("APIs for PCUB  Application"));
    }


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

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

    @Configuration
    public class MailConfig {

        @Bean
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587); // or 465 for SSL
            mailSender.setUsername("team.pcub@gmail.com"); // Replace with your email
            mailSender.setPassword("zfvq pbuk gxeh akwp");    // Use Gmail App Password

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true"); // For TLS
            props.put("mail.debug", "true"); // Optional: log mail session

            return mailSender;
        }
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

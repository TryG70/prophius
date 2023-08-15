package com.trygod.prophiusassessment.config.security;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppConfig {

    private JwtConfig jwtConfig;

    @Data
    public static class JwtConfig{
        private String secretKey;
    }
}

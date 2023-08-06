package com.trygod.prophiusassessment;

import com.trygod.prophiusassessment.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class ProphiusAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProphiusAssessmentApplication.class, args);
    }

}

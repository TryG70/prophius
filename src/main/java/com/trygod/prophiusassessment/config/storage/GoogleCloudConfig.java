package com.trygod.prophiusassessment.config.storage;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Configuration
public class GoogleCloudConfig {

    @Value("${google.cloud.storage.application-credentials}")
    private String GOOGLE_APPLICATION_CREDENTIALS;

    @Value("${google.cloud.storage.project-id}")
    private String STORAGE_PROJECT_ID;


    @Bean
    public Storage getStorage() {
        try{
            Credentials credentials = GoogleCredentials
                    .fromStream(  new FileInputStream(GOOGLE_APPLICATION_CREDENTIALS));

            return StorageOptions.newBuilder().setCredentials(credentials)
                    .setProjectId(STORAGE_PROJECT_ID).build().getService();
        }catch (StorageException | FileNotFoundException exception){
            log.error(exception.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}

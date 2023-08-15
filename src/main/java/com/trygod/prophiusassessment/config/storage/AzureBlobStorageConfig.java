package com.trygod.prophiusassessment.config.storage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    @Value("${azure.storage.blob-endpoint}")
    private String AZURE_STORAGE_ACCOUNT_ENDPOINT;

    @Value("${azure.storage.account-name}")
    private String AZURE_STORAGE_ACCOUNT_NAME;

    @Value("${azure.storage.account-key}")
    private String AZURE_STORAGE_ACCOUNT_KEY;


    @Bean
    public BlobServiceClient getBlobServiceClient() {

        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(AZURE_STORAGE_ACCOUNT_NAME, AZURE_STORAGE_ACCOUNT_KEY);

        return new BlobServiceClientBuilder()
                .endpoint(AZURE_STORAGE_ACCOUNT_ENDPOINT)
                .credential(credential)
                .buildClient();
    }
}

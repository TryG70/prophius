package com.trygod.prophiusassessment.service.storage;


import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.batch.BlobBatchClient;
import com.azure.storage.blob.batch.BlobBatchClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.blob.models.BlobItem;
import com.trygod.prophiusassessment.dto.response.StorageResponse;
import com.trygod.prophiusassessment.exception.StorageException;
import com.trygod.prophiusassessment.exception.StorageFileNotFoundException;
import com.trygod.prophiusassessment.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class AzureBlobStorageService implements StorageService {


    @Value("${azure.storage.container-name}")
    private String containerName;


    private final BlobServiceClient blobServiceClient;

    private BlobContainerClient blobContainerClient;


    @Override
    public void init() {

        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        blobContainerClient.createIfNotExists();
    }


    @Override
    public String store(String fileName, MultipartFile file) {
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(fileName).getBlockBlobClient();

        try (InputStream dataStream = file.getInputStream()) {
            blockBlobClient.upload(dataStream, file.getSize());

            return fileName;

        } catch (IOException ex) {
            log.error("Error uploading file to Azure Blob Storage. File name = " + ex.getMessage());
            throw new StorageException("Error uploading file to Azure Blob Storage. File name = " + fileName, ex);
        }
    }


    @Override
    public StorageResponse details(String key) {

        BlockBlobClient blobClient = blobContainerClient.getBlobClient(key).getBlockBlobClient();

        if(!blobClient.exists()){
            throw new StorageFileNotFoundException("Could not read file: " + key);
        }

        StorageResponse storageResponse = new StorageResponse();
        storageResponse.setKey(blobClient.getCustomerProvidedKey().toString());
        storageResponse.setName(blobClient.getBlobName());
        storageResponse.setUrl(blobClient.getBlobUrl());
        storageResponse.setSize(blobClient.getProperties().getBlobSize());
        return storageResponse;
    }


    @Override
    @SneakyThrows
    public Resource loadAsResource(String key) {

        BlockBlobClient blobClient = blobContainerClient.getBlobClient(key).getBlockBlobClient();

        Resource resource = new UrlResource(blobClient.getBlobUrl());

        if (!resource.exists() || !resource.isReadable()) {
            throw new StorageFileNotFoundException("Could not read file: " + key);
        }
        return resource;

    }


    @Override
    public void deleteAll() {

        List<String> blobNames = blobContainerClient.listBlobs().stream()
                .map(BlobItem::getName)
                .collect(Collectors.toList());

        BlobBatchClient batchClient = new BlobBatchClientBuilder(blobContainerClient).buildClient();
        batchClient.deleteBlobs(blobNames, null);
    }


    @Override
    public void delete(String key) {

        BlockBlobClient blobClient = blobContainerClient.getBlobClient(key).getBlockBlobClient();
        blobClient.deleteIfExists();
    }
}

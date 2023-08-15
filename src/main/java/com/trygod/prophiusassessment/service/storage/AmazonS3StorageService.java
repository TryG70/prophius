package com.trygod.prophiusassessment.service.storage;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AmazonS3StorageService implements StorageService {

    @Value("${aws.storage.bucket-name}")
    private String AMAZON_S3_BUCKET_NAME;


    private final AmazonS3 s3;

    @Override
    public void init() {
        if (!s3.doesBucketExistV2(AMAZON_S3_BUCKET_NAME)) {
            s3.createBucket(AMAZON_S3_BUCKET_NAME);
        }
    }


    @Override
    @SneakyThrows
    public String store(String fileName, MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            s3.putObject(AMAZON_S3_BUCKET_NAME, fileName, inputStream, metadata);

            return fileName;

        } catch (AmazonServiceException e) {
            log.error("Error uploading file to S3: " + e.getErrorMessage());
            throw new StorageException("Error uploading file to S3: " + e.getErrorMessage(), e);
        }
    }


    @Override
    @SneakyThrows
    public StorageResponse details(String key) {

        StorageResponse storageResponse = new StorageResponse();

        try (S3Object s3Object = s3.getObject(AMAZON_S3_BUCKET_NAME, key)) {
            storageResponse.setKey(s3Object.getKey());
            storageResponse.setName(s3Object.getBucketName());
            storageResponse.setSize(s3Object.getObjectMetadata().getContentLength());
            storageResponse.setUrl(s3Object.getObjectContent().getHttpRequest().getURI().toString());

            return storageResponse;

        } catch (AmazonS3Exception e) {
            log.error("Error: " + e.getErrorMessage());
            throw new StorageException("Could not read file: " + key, e);
        }
    }

    @Override
    public Resource loadAsResource(String key) {

        Resource resource = new UrlResource(s3.getUrl(AMAZON_S3_BUCKET_NAME, key));
        if (!resource.exists() || !resource.isReadable()) {
            throw new StorageFileNotFoundException("Could not read file: " + key);
        }
        return resource;
    }


    @Override
    public void deleteAll() {
        ObjectListing object_listing = s3.listObjects(AMAZON_S3_BUCKET_NAME);
        while (true) {
            // Create a list of keys to delete
            List<DeleteObjectsRequest.KeyVersion> keys = object_listing.getObjectSummaries()
                    .stream()
                    .map(summary -> new DeleteObjectsRequest.KeyVersion(summary.getKey()))
                    .collect(Collectors.toList());
            // If there are keys in the list, delete them in a batch
            if (!keys.isEmpty()) {
                s3.deleteObjects(new DeleteObjectsRequest(AMAZON_S3_BUCKET_NAME).withKeys(keys));
            }
            // Check if there are more objects to delete
            if (object_listing.isTruncated()) {
                object_listing = s3.listNextBatchOfObjects(object_listing);
            } else {
                // If there are no more objects to delete, exit the loop
                break;
            }
        }
    }


    @Override
    public void delete(String key) {

        try {
            s3.deleteObject(AMAZON_S3_BUCKET_NAME, key);
        } catch (AmazonS3Exception e) {
            log.error("Failed to delete object: {}", e.getErrorMessage(), e);
            throw new StorageException("Failed to delete object: " + key, e);
        } finally {
            if (s3 != null) {
                s3.shutdown();
            }
        }
    }

}


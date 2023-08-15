package com.trygod.prophiusassessment.service.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.trygod.prophiusassessment.exception.StorageException;
import com.trygod.prophiusassessment.dto.response.StorageResponse;
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

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class GoogleCloudStorageService implements StorageService {


    @Value("${google.cloud.storage.bucket-name}")
    private String GCS_BUCKET_NAME;


    private final Storage storage;


    @Override
    public void init() {
        try {
            if (!storage.get(GCS_BUCKET_NAME).exists()) {
                try {
                    storage.create(BucketInfo.of(GCS_BUCKET_NAME));
                } catch (com.google.cloud.storage.StorageException ex) {
                    log.error("Could not create bucket: " + GCS_BUCKET_NAME, ex);
                    throw new StorageException("Could not create bucket: " + GCS_BUCKET_NAME, ex);
                }
            }
        } catch (com.google.cloud.storage.StorageException ex) {
            log.error("Couldn't access GCP: " + storage, ex);
            throw new StorageException("Couldn't access GCP: " + storage, ex);
        }
    }

    @Override
    @SneakyThrows
    public String store(String fileName, MultipartFile file) {

        BlobId blobId = BlobId.of(GCS_BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.get(GCS_BUCKET_NAME, fileName);

        // Optional: set a generation-match precondition to avoid potential race
        // conditions and data corruptions. The request returns a 412 error if the
        // preconditions are not met.
        Storage.BlobWriteOption precondition = Optional.ofNullable(blob)
                // If the destination already exists in your bucket, instead set a generation-match
                // precondition. This will cause the request to fail if the existing object's generation
                // changes before the request runs.
                .map(b -> Storage.BlobWriteOption.generationMatch(b.getGeneration()))
                // For a target object that does not yet exist, set the DoesNotExist precondition.
                // This will cause the request to fail if the object is created before the request runs.
                .orElse(Storage.BlobWriteOption.doesNotExist());


        try (InputStream inputStream = file.getInputStream()) {
            storage.createFrom(blobInfo, inputStream, precondition);
        } catch (StorageException ex) {
            log.error("Error storing file: " + fileName, ex);
            throw new StorageException("Error storing file: " + fileName, ex);
        }

        return fileName;
    }

    @Override
    public StorageResponse details(String key) {

        Blob blob = storage.get(BlobId.of(GCS_BUCKET_NAME, key));
        if(!blob.exists()) {
            throw new StorageFileNotFoundException("Could not read file: " + key);
        }
        StorageResponse storageResponse = new StorageResponse();
        storageResponse.setKey(key);
        storageResponse.setUrl(blob.getMediaLink());
        storageResponse.setName(blob.getName());
        storageResponse.setSize(blob.getSize());

        return storageResponse;
    }

    @Override
    @SneakyThrows
    public Resource loadAsResource(String key) {

        Blob blob = storage.get(BlobId.of(GCS_BUCKET_NAME, key));
        Resource resource = new UrlResource(blob.getMediaLink());

        if (!resource.exists() || !resource.isReadable()) {
            throw new StorageFileNotFoundException("Could not read file: " + key);
        }

        return resource;
    }

    @Override
    public void deleteAll() {

        Page<Blob> blobs = storage.list(GCS_BUCKET_NAME);

        if(blobs == null) {
            throw new StorageFileNotFoundException("No files found in bucket: " + GCS_BUCKET_NAME + " to delete");
        }

        List<BlobId> blobIds = blobs.streamAll().map(Blob::getBlobId).collect(Collectors.toList());

        if (!blobIds.isEmpty()) {
            storage.delete(blobIds);
        }
    }

    @Override
    public void delete(String key) {

        Blob blob = storage.get(GCS_BUCKET_NAME,    key);

        if (!blob.exists()) {
            throw new StorageFileNotFoundException("Could not read file: " + key);
        }

        // Optional: set a generation-match precondition to avoid potential race
        // conditions and data corruptions. The request to upload returns a 412 error if
        // the object's generation number does not match your precondition.
        Storage.BlobSourceOption precondition =
                Storage.BlobSourceOption.generationMatch(blob.getGeneration());

        boolean deleted = storage.delete(GCS_BUCKET_NAME, key, precondition);

        if (!deleted) {
            throw new StorageException("Could not delete file: " + key);
        }

    }
}

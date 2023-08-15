package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.dto.response.StorageResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    String store(String fileName, MultipartFile file);

    StorageResponse details(String key);

    Resource loadAsResource(String key);

    void deleteAll();

    void delete(String key);
}

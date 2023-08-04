package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface SearchService<T> {

    MessageResponse<Page<T>> search(String search, Pageable pageable);
}

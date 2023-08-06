package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService<V> {

    MessageResponse<Page<V>> search(String search, Pageable pageable);
}

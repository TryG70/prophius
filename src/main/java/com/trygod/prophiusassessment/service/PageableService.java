package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PageableService<V> {

    MessageResponse<Page<V>> findAll(Long id, Pageable pageable);

}

package com.trygod.prophiusassessment.service;

import com.querydsl.core.types.Predicate;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PageableService<T> {

    <U> MessageResponse<Page<U>> findAll(Predicate predicate, Pageable pageable, Class<U> type);

}

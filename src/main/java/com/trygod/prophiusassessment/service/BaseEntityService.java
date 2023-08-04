package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.dto.response.MessageResponse;

public interface BaseEntityService<T, U> {

    MessageResponse<T> create(T request);

    MessageResponse<T> update(Long id, T request);

    void delete(Long id);

    U findById(Long id);


}

package com.trygod.prophiusassessment.service;

public interface BaseEntityService<T, U, V> {

    V create(T request);

    V update(Long id, T request);

    void delete(Long id);

    U findById(Long id);


}

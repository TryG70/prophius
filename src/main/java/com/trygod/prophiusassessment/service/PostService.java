package com.trygod.prophiusassessment.service;

public interface PostService<T, U, V> extends BaseEntityService<T, U, V>, PageableService<V>, SearchService<V>{

    void likePost(Long postId, Long userId);

    void unlikePost(Long postId, Long userId);
}

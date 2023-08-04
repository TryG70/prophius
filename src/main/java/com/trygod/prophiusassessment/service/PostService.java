package com.trygod.prophiusassessment.service;

public interface PostService<T, U> extends BaseEntityService<T, U>, PageableService<T>, SearchService<T>{

    void likePost(Long postId, Long userId);

    void unlikePost(Long postId, Long userId);
}

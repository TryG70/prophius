package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.dto.request.AuthenticateUserDto;

public interface UserService<T, U, V> extends BaseEntityService<T, U, V>, SearchService<V>{

    V findByUsername(String username);

    String authenticateUser(AuthenticateUserDto request);

    void followUser(Long followerId, Long followeeId);

    void unfollowUser(Long followerId, Long followeeId);
}

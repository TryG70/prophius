package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import org.springframework.beans.BeanUtils;

public interface UserService<T, U> extends BaseEntityService<T, U>, SearchService<T>{

    MessageResponse<UserDto> findByUsername(String username);

    void followUser(Long followerId, Long followeeId);

    void unfollowUser(Long followerId, Long followeeId);
}

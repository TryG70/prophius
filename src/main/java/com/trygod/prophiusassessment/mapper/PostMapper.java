package com.trygod.prophiusassessment.mapper;

import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.PostResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostMapper {

    private final UserService<UserDto, UserData, UserResponse>  userService;

    public PostResponse toDTO(PostData data) {
        PostResponse postResponse = new PostResponse();
        BeanUtils.copyProperties(data, postResponse);
        postResponse.setUserId(data.getUser().getId());
        return postResponse;
    }

    public PostData toEntity(PostDto dto) {
        PostData postData = new PostData();
        BeanUtils.copyProperties(dto, postData);
        postData.setUser(userService.findById(dto.getUserId()));
        return postData;
    }
}

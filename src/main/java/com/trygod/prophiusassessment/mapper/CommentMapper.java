package com.trygod.prophiusassessment.mapper;

import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.CommentResponse;
import com.trygod.prophiusassessment.dto.response.PostResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.service.PostService;
import com.trygod.prophiusassessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final PostService<PostDto, PostData, PostResponse> postService;

    private final UserService<UserDto, UserData, UserResponse> userService;

    public CommentResponse toDTO(CommentData data) {
        CommentResponse commentResponse = new CommentResponse();
        BeanUtils.copyProperties(data, commentResponse);
        commentResponse.setUserId(data.getUser().getId());
        commentResponse.setPostId(data.getPost().getId());
        return commentResponse;
    }

    public CommentData toEntity(CommentDto dto) {
        CommentData commentData = new CommentData();
        BeanUtils.copyProperties(dto, commentData);
        commentData.setUser(userService.findById(dto.getUserId()));
        commentData.setPost(postService.findById(dto.getPostId()));
        return commentData;
    }
}

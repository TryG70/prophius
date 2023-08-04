package com.trygod.prophiusassessment.mapper;

import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.service.PostService;
import com.trygod.prophiusassessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final PostService<PostDto, PostData> postService;

    private final UserService<UserDto, UserData> userService;

    public CommentDto toDTO(CommentData data) {
        CommentDto commentDto = new CommentDto();
        BeanUtils.copyProperties(data, commentDto);
        commentDto.setUserId(data.getUser().getId());
        commentDto.setPostId(data.getPost().getId());
        return commentDto;
    }

    public CommentData toEntity(CommentDto dto) {
        CommentData commentData = new CommentData();
        BeanUtils.copyProperties(dto, commentData);
        commentData.setUser(userService.findById(dto.getUserId()));
        commentData.setPost(postService.findById(dto.getPostId()));
        return commentData;
    }
}

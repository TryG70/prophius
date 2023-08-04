package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.mapper.PostMapper;
import com.trygod.prophiusassessment.repository.PostRepository;
import com.trygod.prophiusassessment.service.NotificationService;
import com.trygod.prophiusassessment.service.PostService;
import com.trygod.prophiusassessment.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService<PostDto, PostData> {

    private final PostRepository postRepository;

    private final UserService<UserDto, UserData> userService;

    private final NotificationService notificationService;

    private final PostMapper postMapper;

    @Override
    public MessageResponse<PostDto> create(PostDto request) {
        PostData postData = postMapper.toEntity(request);
        postData = postRepository.save(postData);
        return MessageResponse.response(postMapper.toDTO(postData));
    }

    @Override
    public MessageResponse<PostDto> update(Long id, PostDto request) {
        PostData savedPostData = findById(id);
        PostData postData = postMapper.toEntity(request);
        BeanUtils.copyProperties(postData, savedPostData);
        savedPostData = postRepository.save(savedPostData);
        return MessageResponse.response(postMapper.toDTO(savedPostData));
    }

    @Override
    public void delete(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
        } else {
            throw new NotFoundException(PostData.class, id);
        }
    }

    @Override
    public PostData findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException(PostData.class, id));
    }

    @Override
    public MessageResponse<Page<PostDto>> findAll(Pageable pageable) {
        Page<PostData> posts = postRepository.findAll(pageable);
        return MessageResponse.response(posts.map(postMapper::toDTO));
    }


    @Override
    public MessageResponse<Page<PostDto>> search(String keyword, Pageable pageable) {
        Page<PostData> posts;
        if (keyword != null && !keyword.isEmpty()) {
            posts = postRepository.findAllByContentContainingIgnoreCase(keyword, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }
        return MessageResponse.response(posts.map(postMapper::toDTO));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void likePost(Long postId, Long userId) {
        PostData postData = findById(postId);
        UserData userData = userService.findById(userId);
        postData.getLikedBy().add(userData);
        postData.setLikeCount(postData.getLikedBy().size());
        postRepository.save(postData);
        notificationService.notifyUser(postData.getUser().getId(), userData.getUsername() + " liked your post");
    }

    @Override
    public void unlikePost(Long postId, Long userId) {
        PostData postData = findById(postId);
        UserData userData = userService.findById(userId);
        postData.getLikedBy().remove(userData);
        postData.setLikeCount(postData.getLikedBy().size());
        postRepository.save(postData);
    }
}

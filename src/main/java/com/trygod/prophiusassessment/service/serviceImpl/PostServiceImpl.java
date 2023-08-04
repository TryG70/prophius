package com.trygod.prophiusassessment.service.serviceImpl;

import com.querydsl.core.types.Predicate;
import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.mapper.PostMapper;
import com.trygod.prophiusassessment.repository.PostRepository;
import com.trygod.prophiusassessment.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService<PostDto, PostData> {

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    @Override
    public MessageResponse<PostDto> create(PostDto request) {
        PostData postData = postMapper.toEntity(request);
        postData = postRepository.save(postData);
        return messageResponse(postMapper.toDTO(postData));
    }

    @Override
    public MessageResponse<PostDto> update(Long id, PostDto request) {
        PostData savedPostData = findById(id);
        PostData postData = postMapper.toEntity(request);
        BeanUtils.copyProperties(postData, savedPostData);
        savedPostData = postRepository.save(savedPostData);
        return messageResponse(postMapper.toDTO(savedPostData));
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
    public <U> MessageResponse<Page<U>> findAll(Predicate predicate, Pageable pageable, Class<U> type) {
        if(type == PostData.class) {
            return messageResponse((Page<U>) postRepository.findAll(predicate, pageable));
        } else {
            return messageResponse(Page.empty());
        }
    }

    @Override
    public MessageResponse<Page<PostDto>> search(String search, PageRequest pageRequest) {
        Page<PostDto> userDtoPage = postRepository.findAll(new UserData().buildPredicate(search), pageRequest)
                .map(postMapper::toDTO);
        return messageResponse(userDtoPage);
    }

    private <T> MessageResponse<T> messageResponse(T data) {
        MessageResponse<T> response = new MessageResponse<>();
        response.setResult(data);
        return response;
    }
}

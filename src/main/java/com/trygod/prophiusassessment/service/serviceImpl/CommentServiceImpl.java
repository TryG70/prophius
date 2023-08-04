package com.trygod.prophiusassessment.service.serviceImpl;

import com.querydsl.core.types.Predicate;
import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.mapper.CommentMapper;
import com.trygod.prophiusassessment.repository.CommentRepository;
import com.trygod.prophiusassessment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService<CommentDto, CommentData> {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public MessageResponse<CommentDto> create(CommentDto request) {
        CommentData commentData = commentMapper.toEntity(request);
        commentData = commentRepository.save(commentData);
        return messageResponse(commentMapper.toDTO(commentData));
    }

    @Override
    public MessageResponse<CommentDto> update(Long id, CommentDto request) {
        CommentData savedCommentData = findById(id);
        CommentData commentData = commentMapper.toEntity(request);
        BeanUtils.copyProperties(commentData, savedCommentData);
        savedCommentData = commentRepository.save(savedCommentData);
        return messageResponse(commentMapper.toDTO(savedCommentData));

    }

    @Override
    public void delete(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new NotFoundException(CommentData.class, id);
        }
    }

    @Override
    public CommentData findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(CommentData.class, id));
    }

    @Override
    public <U> MessageResponse<Page<U>> findAll(Predicate predicate, Pageable pageable, Class<U> type) {
        if(type == CommentData.class) {
            return messageResponse((Page<U>) commentRepository.findAll(predicate, pageable));
        } else {
            return messageResponse(Page.empty());
        }
    }

    private <T> MessageResponse<T> messageResponse(T data) {
        MessageResponse<T> response = new MessageResponse<>();
        response.setResult(data);
        return response;
    }
}

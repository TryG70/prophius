package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.response.CommentResponse;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.NotificationResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.mapper.CommentMapper;
import com.trygod.prophiusassessment.repository.CommentRepository;
import com.trygod.prophiusassessment.service.CommentService;
import com.trygod.prophiusassessment.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService<CommentDto, CommentData, CommentResponse> {

    private final CommentRepository commentRepository;

    private final NotificationService<NotificationResponse, NotificationData> notificationService;

    private final CommentMapper commentMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommentResponse create(CommentDto request) {
        CommentData commentData = commentMapper.toEntity(request);
        commentData = commentRepository.save(commentData);
        Long postOwnerId = commentData.getPost().getUser().getId();
        notificationService.notifyUser(postOwnerId, commentData.getUser().getUsername() + " commented on your post");
        return commentMapper.toDTO(commentData);
    }

    @Override
    public CommentResponse update(Long id, CommentDto request) {
        CommentData savedCommentData = findById(id);
        CommentData commentData = commentMapper.toEntity(request);
        BeanUtils.copyProperties(commentData, savedCommentData);
        savedCommentData = commentRepository.save(savedCommentData);
        return commentMapper.toDTO(savedCommentData);

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
    public MessageResponse<Page<CommentResponse>> findAll(Long id, Pageable pageable) {
        Page<CommentData> commentDataPage = commentRepository.findAllByPost_Id(id, pageable);
        return MessageResponse.response(commentDataPage.map(commentMapper::toDTO));
    }
}

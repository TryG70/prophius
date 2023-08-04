package com.trygod.prophiusassessment.service.serviceImpl;

import com.querydsl.core.types.Predicate;
import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService<CommentDto, CommentData> {
    @Override
    public MessageResponse<CommentDto> create(CommentDto request) {
        return null;
    }

    @Override
    public MessageResponse<CommentDto> update(Long id, CommentDto request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public CommentData findById(Long id) {
        return null;
    }

    @Override
    public <U> MessageResponse<Page<U>> findAll(Predicate predicate, Pageable pageable, Class<U> type) {
        return null;
    }
}

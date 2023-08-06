package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.response.CommentResponse;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.NotificationResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.mapper.CommentMapper;
import com.trygod.prophiusassessment.repository.CommentRepository;
import com.trygod.prophiusassessment.repository.PostRepository;
import com.trygod.prophiusassessment.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private PostRepository mockPostRepository;
    @Mock
    private NotificationService<NotificationResponse, NotificationData> mockNotificationService;
    @Mock
    private CommentMapper mockCommentMapper;
    @InjectMocks
    private CommentServiceImpl commentServiceImplUnderTest;

    private CommentDto request;
    private CommentResponse expectedResult;
    private CommentData commentData;
    private CommentData savedCommentData;
    private CommentResponse commentResponse;

    private PostData postData;

    @BeforeEach
    void setUp() {

        UserData user = new UserData();
        user.setId(1L);
        user.setUsername("username");

        postData = new PostData();
        postData.setContent("content");
        postData.setUser(user);

        commentData = new CommentData();
        commentData.setContent("content");
        commentData.setUser(user);
        commentData.setPost(postData);

        request = new CommentDto();
        request.setContent("content");
        request.setPostId(1L);
        request.setUserId(1L);

        expectedResult = new CommentResponse();
        expectedResult.setId(0L);
        expectedResult.setContent("content");
        expectedResult.setPostId(1L);
        expectedResult.setUserId(1L);

        savedCommentData = new CommentData();
        savedCommentData.setId(0L);
        BeanUtils.copyProperties(commentData, savedCommentData);

        commentResponse = new CommentResponse();
        commentResponse.setId(0L);
        commentResponse.setContent("content");
        commentResponse.setPostId(1L);
        commentResponse.setUserId(1L);
    }

    @Test
    void testCreate() {

        when(mockCommentMapper.toEntity(request)).thenReturn(commentData);
        when(mockCommentRepository.save(commentData)).thenReturn(savedCommentData);
        when(mockCommentMapper.toDTO(savedCommentData)).thenReturn(commentResponse);
        when(mockPostRepository.save(postData)).thenReturn(null);

        CommentResponse result = commentServiceImplUnderTest.create(request);

        assertThat(result).isEqualTo(expectedResult);
        verify(mockCommentRepository, times(1)).save(commentData);
        verify(mockPostRepository, times(1)).save(postData);
        verify(mockCommentMapper, times(1)).toEntity(request);
        verify(mockCommentMapper, times(1)).toDTO(savedCommentData);
        verify(mockNotificationService, times(1)).notifyUser(eq(1L), any(String.class));
    }

    @Test
    void testUpdate() {

        Optional<CommentData> optionalCommentData = Optional.of(savedCommentData);
        when(mockCommentRepository.findById(0L)).thenReturn(optionalCommentData);

        when(mockCommentMapper.toEntity(request)).thenReturn(commentData);
        when(mockCommentRepository.save(commentData)).thenReturn(savedCommentData);
        when(mockCommentMapper.toDTO(savedCommentData)).thenReturn(commentResponse);

        CommentResponse result = commentServiceImplUnderTest.update(0L, request);

        assertThat(result).isEqualTo(expectedResult);
        verify(mockCommentRepository, times(1)).save(commentData);
        verify(mockCommentMapper, times(1)).toEntity(request);
        verify(mockCommentMapper, times(1)).toDTO(savedCommentData);
        verify(mockCommentRepository, times(1)).findById(0L);
    }

    @Test
    void testDelete() {
        when(mockCommentRepository.existsById(0L)).thenReturn(true);

        commentServiceImplUnderTest.delete(0L);

        verify(mockCommentRepository, times(1)).deleteById(0L);
        verify(mockCommentRepository, times(1)).existsById(0L);
    }

    @Test
    void testDelete_CommentRepositoryExistsByIdReturnsFalse() {

        when(mockCommentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> commentServiceImplUnderTest.delete(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity CommentData with id 1 not found");
    }

    @Test
    void testFindById() {

        final Optional<CommentData> optionalCommentData = Optional.of(commentData);
        when(mockCommentRepository.findById(1L)).thenReturn(optionalCommentData);

        final CommentData result = commentServiceImplUnderTest.findById(1L);

        assertThat(result).isEqualTo(commentData);
        verify(mockCommentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_CommentRepositoryReturnsAbsent() {

        when(mockCommentRepository.findById(0L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentServiceImplUnderTest.findById(0L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity CommentData with id 0 not found");
    }

    @Test
    void testFindAll() {

        final Page<CommentData> commentDataPage = new PageImpl<>(List.of(commentData));
        final Page<CommentResponse> commentResponsePage = new PageImpl<>(List.of(expectedResult));
        when(mockCommentRepository.findAllByPost_Id(eq(1L), any(Pageable.class)))
                .thenReturn(commentDataPage);
        when(mockCommentMapper.toDTO(commentData)).thenReturn(expectedResult);

        final MessageResponse<Page<CommentResponse>> result = commentServiceImplUnderTest.findAll(1L, PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(commentResponsePage));
        verify(mockCommentRepository, times(1)).findAllByPost_Id(eq(1L), any(Pageable.class));
        verify(mockCommentMapper, times(1)).toDTO(commentData);
    }

    @Test
    void testFindAll_CommentRepositoryReturnsNoItems() {

        when(mockCommentRepository.findAllByPost_Id(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final MessageResponse<Page<CommentResponse>> result = commentServiceImplUnderTest.findAll(1L, PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(new PageImpl<>(Collections.emptyList())));
        verify(mockCommentRepository, times(1)).findAllByPost_Id(eq(1L), any(Pageable.class));
    }
}

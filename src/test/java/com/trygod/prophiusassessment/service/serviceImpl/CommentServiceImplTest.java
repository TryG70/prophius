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

    @BeforeEach
    void setUp() {
        // Set up user
        UserData user = new UserData();
        user.setId(1L);
        user.setUsername("username");

        // Set up post data
        PostData postData = new PostData();
        postData.setContent("content");
        postData.setUser(user);

        // Set up comment data
        commentData = new CommentData();
        commentData.setContent("content");
        commentData.setUser(user);
        commentData.setPost(postData);

        // Set up request
        request = new CommentDto();
        request.setContent("content");
        request.setPostId(1L);
        request.setUserId(1L);

        // Set up expected result
        expectedResult = new CommentResponse();
        expectedResult.setId(0L);
        expectedResult.setContent("content");
        expectedResult.setPostId(1L);
        expectedResult.setUserId(1L);

        // Set up saved comment data
        savedCommentData = new CommentData();
        savedCommentData.setId(0L);
        BeanUtils.copyProperties(commentData, savedCommentData);

        // Set up comment response
        commentResponse = new CommentResponse();
        commentResponse.setId(0L);
        commentResponse.setContent("content");
        commentResponse.setPostId(1L);
        commentResponse.setUserId(1L);
    }

    @Test
    void testCreate() {
        // Mocking behavior for creating a comment
        when(mockCommentMapper.toEntity(request)).thenReturn(commentData);
        when(mockCommentRepository.save(commentData)).thenReturn(savedCommentData);
        when(mockCommentMapper.toDTO(savedCommentData)).thenReturn(commentResponse);

        // Call the service method
        CommentResponse result = commentServiceImplUnderTest.create(request);

        // Verify the result and interactions
        assertThat(result).isEqualTo(expectedResult);
        verify(mockCommentRepository, times(1)).save(commentData);
        verify(mockCommentMapper, times(1)).toEntity(request);
        verify(mockCommentMapper, times(1)).toDTO(savedCommentData);
        verify(mockNotificationService, times(1)).notifyUser(eq(1L), any(String.class));
    }

    @Test
    void testUpdate() {
        // Mocking behavior for updating a comment
        Optional<CommentData> optionalCommentData = Optional.of(savedCommentData);
        when(mockCommentRepository.findById(0L)).thenReturn(optionalCommentData);

        when(mockCommentMapper.toEntity(request)).thenReturn(commentData);
        when(mockCommentRepository.save(commentData)).thenReturn(savedCommentData);
        when(mockCommentMapper.toDTO(savedCommentData)).thenReturn(commentResponse);

        // Call the service method
        CommentResponse result = commentServiceImplUnderTest.update(0L, request);

        // Verify the result and interactions
        assertThat(result).isEqualTo(expectedResult);
        verify(mockCommentRepository, times(1)).save(commentData);
        verify(mockCommentMapper, times(1)).toEntity(request);
        verify(mockCommentMapper, times(1)).toDTO(savedCommentData);
        verify(mockCommentRepository, times(1)).findById(0L);
    }

    @Test
    void testDelete() {
        // Mocking behavior for deleting a comment
        when(mockCommentRepository.existsById(0L)).thenReturn(true);

        // Call the service method
        commentServiceImplUnderTest.delete(0L);

        // Verify the interactions
        verify(mockCommentRepository, times(1)).deleteById(0L);
        verify(mockCommentRepository, times(1)).existsById(0L);
    }

    @Test
    void testDelete_CommentRepositoryExistsByIdReturnsFalse() {
        // Mocking behavior for deleting a comment that doesn't exist
        when(mockCommentRepository.existsById(1L)).thenReturn(false);

        // Verify that NotFoundException is thrown when deleting a non-existing comment
        assertThatThrownBy(() -> commentServiceImplUnderTest.delete(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity CommentData with id 1 not found");
    }

    @Test
    void testFindById() {
        // Mocking behavior for finding a comment by ID
        final Optional<CommentData> optionalCommentData = Optional.of(commentData);
        when(mockCommentRepository.findById(1L)).thenReturn(optionalCommentData);

        // Call the service method
        final CommentData result = commentServiceImplUnderTest.findById(1L);

        // Verify the result and interactions
        assertThat(result).isEqualTo(commentData);
        verify(mockCommentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_CommentRepositoryReturnsAbsent() {
        // Mocking behavior for finding a non-existing comment by ID
        when(mockCommentRepository.findById(0L)).thenReturn(Optional.empty());

        // Verify that NotFoundException is thrown when finding a non-existing comment
        assertThatThrownBy(() -> commentServiceImplUnderTest.findById(0L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity CommentData with id 0 not found");
    }

    @Test
    void testFindAll() {
        // Mocking behavior for finding all comments
        final Page<CommentData> commentDataPage = new PageImpl<>(List.of(commentData));
        final Page<CommentResponse> commentResponsePage = new PageImpl<>(List.of(expectedResult));
        when(mockCommentRepository.findAllByPost_Id(eq(1L), any(Pageable.class)))
                .thenReturn(commentDataPage);
        when(mockCommentMapper.toDTO(commentData)).thenReturn(expectedResult);

        // Call the service method
        final MessageResponse<Page<CommentResponse>> result = commentServiceImplUnderTest.findAll(1L, PageRequest.of(0, 1));

        // Verify the result and interactions
        assertThat(result).isEqualTo(MessageResponse.response(commentResponsePage));
        verify(mockCommentRepository, times(1)).findAllByPost_Id(eq(1L), any(Pageable.class));
        verify(mockCommentMapper, times(1)).toDTO(commentData);
    }

    @Test
    void testFindAll_CommentRepositoryReturnsNoItems() {
        // Mocking behavior for finding all comments when no comments are available
        when(mockCommentRepository.findAllByPost_Id(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Call the service method
        final MessageResponse<Page<CommentResponse>> result = commentServiceImplUnderTest.findAll(1L, PageRequest.of(0, 1));

        // Verify the result and interactions
        assertThat(result).isEqualTo(MessageResponse.response(new PageImpl<>(Collections.emptyList())));
        verify(mockCommentRepository, times(1)).findAllByPost_Id(eq(1L), any(Pageable.class));
    }
}

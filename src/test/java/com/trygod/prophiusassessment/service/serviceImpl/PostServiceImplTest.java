
package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.NotificationResponse;
import com.trygod.prophiusassessment.dto.response.PostResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.mapper.PostMapper;
import com.trygod.prophiusassessment.repository.PostRepository;
import com.trygod.prophiusassessment.service.NotificationService;
import com.trygod.prophiusassessment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository mockPostRepository;
    @Mock
    private UserService<UserDto, UserData, UserResponse> mockUserService;
    @Mock
    private NotificationService<NotificationResponse, NotificationData> mockNotificationService;
    @Mock
    private PostMapper mockPostMapper;

    @InjectMocks
    private PostServiceImpl postServiceImplUnderTest;

    private PostDto request;

    private PostResponse expectedResult;

    private PostData postData;

    private UserData user;

    private PostData savedPostData;

    private PostResponse postResponse;

    @BeforeEach
    void setUp() {

        request = new PostDto();
        request.setContent("content");
        request.setUserId(1L);

        expectedResult = new PostResponse();
        expectedResult.setId(0L);
        expectedResult.setContent("content");
        expectedResult.setUserId(1L);

        user = new UserData();
        user.setId(1L);
        user.setUsername("username");

        postData = new PostData();
        postData.setContent("content");
        postData.setUser(user);

        savedPostData = new PostData();
        savedPostData.setId(0L);

        postResponse = new PostResponse();
        postResponse.setId(0L);
        postResponse.setContent("content");
        postResponse.setUserId(1L);
    }

    @Test
    void testCreate() {

        when(mockPostMapper.toEntity(request)).thenReturn(postData);
        when(mockPostRepository.save(postData)).thenReturn(savedPostData);
        when(mockPostMapper.toDTO(savedPostData)).thenReturn(postResponse);

        PostResponse result = postServiceImplUnderTest.create(request);

        assertThat(result).isEqualTo(expectedResult);
        verify(mockPostRepository, times(1)).save(postData);
        verify(mockPostMapper, times(1)).toEntity(request);
        verify(mockPostMapper, times(1)).toDTO(savedPostData);

    }

    @Test
    void testUpdate() {
        Optional<PostData> optionalPostData = Optional.of(savedPostData);
        when(mockPostRepository.findById(0L)).thenReturn(optionalPostData);

        when(mockPostMapper.toEntity(request)).thenReturn(postData);
        when(mockPostRepository.save(postData)).thenReturn(savedPostData);
        when(mockPostMapper.toDTO(savedPostData)).thenReturn(postResponse);

        PostResponse result = postServiceImplUnderTest.update(0L,request);

        assertThat(result).isEqualTo(expectedResult);
        verify(mockPostRepository, times(1)).save(postData);
        verify(mockPostMapper, times(1)).toEntity(request);
        verify(mockPostMapper, times(1)).toDTO(savedPostData);
        verify(mockPostRepository, times(1)).findById(0L);

    }

    @Test
    void testDelete() {

        when(mockPostRepository.existsById(0L)).thenReturn(true);

        postServiceImplUnderTest.delete(0L);

        verify(mockPostRepository, times(1)).deleteById(0L);
        verify(mockPostRepository, times(1)).existsById(0L);
    }

    @Test
    void testDelete_PostRepositoryExistsByIdReturnsFalse() {

        when(mockPostRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> postServiceImplUnderTest.delete(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity %s with id %d not found", PostData.class.getSimpleName(), 1L);
    }

    @Test
    void testFindById() {

        final Optional<PostData> optionalUserData = Optional.of(postData);
        when(mockPostRepository.findById(1L)).thenReturn(optionalUserData);

        final PostData result = postServiceImplUnderTest.findById(1L);

        assertThat(result).isEqualTo(postData);
        verify(mockPostRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_PostRepositoryReturnsAbsent() {
        doThrow(new NotFoundException("Entity " + PostData.class.getSimpleName() + " with id " + 0L + " not found")).when(mockPostRepository)
                .findById(0L);

        assertThatThrownBy(() -> postServiceImplUnderTest.findById(0L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity %s with id %d not found", PostData.class.getSimpleName(), 0L);
    }

    @Test
    void testFindAll() {

        final Page<PostData> postDataPage = new PageImpl<>(List.of(postData));
        final Page<PostResponse> postResponsePage = new PageImpl<>(List.of(expectedResult));
        when(mockPostRepository.findAllByUser_IdOrderByCreatedDateDesc(eq(0L), any(Pageable.class)))
                .thenReturn(postDataPage);

        when(mockPostMapper.toDTO(postData)).thenReturn(postResponse);

        final MessageResponse<Page<PostResponse>> result = postServiceImplUnderTest.findAll(0L, PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(postResponsePage));
        verify(mockPostRepository, times(1)).findAllByUser_IdOrderByCreatedDateDesc(eq(0L), any(Pageable.class));
        verify(mockPostMapper, times(1)).toDTO(postData);
    }

    @Test
    void testFindAll_PostRepositoryReturnsNoItems() {

        when(mockPostRepository.findAllByUser_IdOrderByCreatedDateDesc(eq(0L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final MessageResponse<Page<PostResponse>> result = postServiceImplUnderTest.findAll(0L, PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(new PageImpl<>(Collections.emptyList())));
        verify(mockPostRepository, times(1)).findAllByUser_IdOrderByCreatedDateDesc(eq(0L), any(Pageable.class));
    }

    @Test
    void testSearch() {
        final Page<PostData> postDataPage = new PageImpl<>(List.of(postData));
        final Page<PostResponse> postResponsePage = new PageImpl<>(List.of(expectedResult));
        when(mockPostRepository.findAllByContentContainingIgnoreCase(eq("con"), any(Pageable.class)))
                .thenReturn(postDataPage);
        when(mockPostMapper.toDTO(postData)).thenReturn(postResponse);

        final MessageResponse<Page<PostResponse>> result = postServiceImplUnderTest.search("con",
                PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(postResponsePage));
        verify(mockPostRepository, times(1)).findAllByContentContainingIgnoreCase(eq("con"), any(Pageable.class));
        verify(mockPostMapper, times(postResponsePage.getSize())).toDTO(postData);
    }

    @Test
    void testSearch_PostRepositoryFindAllByContentContainingIgnoreCaseReturnsNoItems() {
        when(mockPostRepository.findAllByContentContainingIgnoreCase(eq("keyWord"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final MessageResponse<Page<PostResponse>> result = postServiceImplUnderTest.search("keyWord",
                PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(new PageImpl<>(Collections.emptyList())));
        verify(mockPostRepository, times(1)).findAllByContentContainingIgnoreCase(eq("keyWord"), any(Pageable.class));
    }

    @Test
    void testLikePost() {
        final Optional<PostData> optionalPostData = Optional.of(postData);
        when(mockPostRepository.findById(0L)).thenReturn(optionalPostData);

        when(mockUserService.findById(1L)).thenReturn(user);

        postServiceImplUnderTest.likePost(0L, 1L);

        verify(mockPostRepository, times(1)).findById(0L);
        verify(mockUserService, times(1)).findById(1L);
        verify(mockPostRepository, times(1)).save(postData);
        verify(mockNotificationService, times(1)).notifyUser(1L, user.getUsername() + " liked your post");
    }

    @Test
    void testUnlikePost() {
        final Optional<PostData> optionalPostData = Optional.of(postData);
        when(mockPostRepository.findById(0L)).thenReturn(optionalPostData);

        when(mockUserService.findById(1L)).thenReturn(user);

        postServiceImplUnderTest.unlikePost(0L, 1L);

        verify(mockPostRepository, times(1)).findById(0L);
        verify(mockUserService, times(1)).findById(1L);
        verify(mockPostRepository, times(1)).save(postData);
    }
}

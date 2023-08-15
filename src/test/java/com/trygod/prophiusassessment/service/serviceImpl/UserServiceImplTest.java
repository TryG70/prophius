package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.config.security.jwt.JwtUtil;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.request.AuthenticateUserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.repository.UserRepository;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private JwtUtil mockJwtUtil;
    @Mock
    private BCryptPasswordEncoder mockPasswordEncoder;
    @Mock
    private AuthenticationManager mockAuthenticationManager;

    @InjectMocks
    private UserServiceImpl userServiceImplUnderTest;

    private UserDto request;

    private UserResponse expectedResult;

    private UserData userData;

    private UserData entity;

    @BeforeEach
    void setUp() {

        request = new UserDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setEmail("email");
        request.setProfilePicture("profilePicture");

        expectedResult = new UserResponse();
        expectedResult.setId(1L);
        expectedResult.setUsername("username");
        expectedResult.setEmail("email");
        expectedResult.setProfilePicture("profilePicture");

        userData = new UserData();
        userData.setId(1L);
        userData.setUsername("username");
        userData.setPassword("password");
        userData.setEmail("email");
        userData.setProfilePicture("profilePicture");

        entity = new UserData();
        entity.setUsername("username");
        entity.setPassword("password");
        entity.setEmail("email");
        entity.setProfilePicture("profilePicture");
    }

    @Test
    void testCreate() {

        when(mockPasswordEncoder.encode("password")).thenReturn("password");
        when(mockUserRepository.save(entity)).thenReturn(userData);

        final UserResponse result = userServiceImplUnderTest.create(request);

        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserRepository, times(1)).save(entity);
        verify(mockPasswordEncoder, times(1)).encode(entity.getPassword());
    }

    @Test
    void testAuthenticateUser() {

        final AuthenticateUserDto request = new AuthenticateUserDto();
        request.setEmail("email");
        request.setPassword("password");

        final Authentication authentication = mock(Authentication.class);
        when(mockAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("email", "password"))).thenReturn(authentication);

        when(mockJwtUtil.generateToken("email")).thenReturn("result");

        final String result = userServiceImplUnderTest.authenticateUser(request);

        assertThat(result).isEqualTo("result");
        verify(mockAuthenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken("email", "password"));
        verify(mockJwtUtil, times(1)).generateToken("email");
    }

    @Test
    void testAuthenticateUser_AuthenticationManagerThrowsAuthenticationException() {

        final AuthenticateUserDto request = new AuthenticateUserDto();
        request.setEmail("email");
        request.setPassword("password");

        doThrow(new BadCredentialsException("Login failed")).when(mockAuthenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken("email", "password"));


        assertThatThrownBy(() -> userServiceImplUnderTest.authenticateUser(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Login failed");
    }

    @Test
    void testUpdate() {

        final Optional<UserData> optionalUserData = Optional.of(userData);
        when(mockUserRepository.findById(1L)).thenReturn(optionalUserData);
        when(mockUserRepository.save(userData)).thenReturn(userData);

        final UserResponse result = userServiceImplUnderTest.update(1L, request);

        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserRepository, times(1)).findById(1L);
        verify(mockUserRepository, times(1)).save(userData);
    }

    @Test
    void testDelete() {

        when(mockUserRepository.existsById(1L)).thenReturn(true);

        userServiceImplUnderTest.delete(1L);

        verify(mockUserRepository, times(1)).deleteById(1L);
        verify(mockUserRepository, times(1)).existsById(1L);
    }

    @Test
    void testDelete_UserRepositoryExistsByIdReturnsFalse() {

        when(mockUserRepository.existsById(0L)).thenReturn(false);

        assertThatThrownBy(() -> userServiceImplUnderTest.delete(0L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity %s with id %d not found", UserData.class.getSimpleName(), 0L);
    }

    @Test
    void testFindById() {

        final Optional<UserData> optionalUserData = Optional.of(userData);
        when(mockUserRepository.findById(1L)).thenReturn(optionalUserData);

        final UserData result = userServiceImplUnderTest.findById(1L);

        assertThat(result).isEqualTo(userData);
        verify(mockUserRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserRepositoryReturnsAbsent() {
        doThrow(new NotFoundException("Entity " + UserData.class.getSimpleName() + " with id " + 0L + " not found")).when(mockUserRepository)
                .findById(0L);

        assertThatThrownBy(() -> userServiceImplUnderTest.findById(0L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity %s with id %d not found", UserData.class.getSimpleName(), 0L);
    }

    @Test
    void testSearch() {
        final Page<UserData> userDataPage = new PageImpl<>(List.of(userData));
        final Page<UserResponse> userResponsePage = new PageImpl<>(List.of(expectedResult));
        when(mockUserRepository.findAllByUsernameContainingIgnoreCase(eq("user"), any(Pageable.class)))
                .thenReturn(userDataPage);

        final MessageResponse<Page<UserResponse>> result = userServiceImplUnderTest.search("user",
                PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(userResponsePage));
        verify(mockUserRepository, times(1)).findAllByUsernameContainingIgnoreCase(eq("user"), any(Pageable.class));
    }

    @Test
    void testSearch_UserRepositoryReturnsNoItems() {

        when(mockUserRepository.findAllByUsernameContainingIgnoreCase(eq("keyWord"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final MessageResponse<Page<UserResponse>> result = userServiceImplUnderTest.search("keyWord",
                PageRequest.of(0, 1));

        assertThat(result).isEqualTo(MessageResponse.response(new PageImpl<>(Collections.emptyList())));
        verify(mockUserRepository, times(1)).findAllByUsernameContainingIgnoreCase(eq("keyWord"), any(Pageable.class));
    }

    @Test
    void testFindByUsername() {

        final Optional<UserData> optionalUserData = Optional.of(userData);
        when(mockUserRepository.findByUsername("username")).thenReturn(optionalUserData);

        final UserResponse result = userServiceImplUnderTest.findByUsername("username");

        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserRepository, times(1)).findByUsername("username");
    }

    @Test
    void testFindByUsername_UserRepositoryReturnsAbsent() {

        doThrow(new NotFoundException("Entity " + UserData.class.getSimpleName() + " with username kachasi not found")).when(mockUserRepository)
                .findByUsername("kachasi");

        assertThatThrownBy(() -> userServiceImplUnderTest.findByUsername("kachasi"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Entity " + UserData.class.getSimpleName() + " with username kachasi not found");
    }

    @Test
    void testFollowUser() {

        final UserData userData1 = new UserData();
        userData1.setId(2L);
        userData1.setUsername("username");
        userData1.setPassword("password");
        userData1.setEmail("email");
        userData1.setProfilePicture("profilePicture");
        final Optional<UserData> optionalUserData = Optional.of(userData);
        final Optional<UserData> optionalUserData1 = Optional.of(userData1);
        when(mockUserRepository.findById(1L)).thenReturn(optionalUserData);
        when(mockUserRepository.findById(2L)).thenReturn(optionalUserData1);

        userServiceImplUnderTest.followUser(1L, 2L);

        assertThat(userData.getFollowing()).containsExactly(userData1);
        assertThat(userData1.getFollowers()).containsExactly(userData);
        verify(mockUserRepository, times(1)).save(userData);
        verify(mockUserRepository, times(1)).findById(1L);
        verify(mockUserRepository, times(1)).findById(2L);
    }

    @Test
    void testUnfollowUser() {

        final UserData userData1 = new UserData();
        userData1.setId(2L);
        userData1.setUsername("username");
        userData1.setPassword("password");
        userData1.setEmail("email");
        userData1.setProfilePicture("profilePicture");
        userData.getFollowers().add(userData1);
        userData1.getFollowing().add(userData);
        final Optional<UserData> optionalUserData = Optional.of(userData);
        final Optional<UserData> optionalUserData1 = Optional.of(userData1);
        when(mockUserRepository.findById(1L)).thenReturn(optionalUserData);
        when(mockUserRepository.findById(2L)).thenReturn(optionalUserData1);

        userServiceImplUnderTest.unfollowUser(1L, 2L);

        assertThat(userData.getFollowers()).doesNotContain(userData1);
        assertThat(userData1.getFollowing()).doesNotContain(userData);
        verify(mockUserRepository, times(1)).save(userData);
        verify(mockUserRepository, times(1)).save(userData1);
        verify(mockUserRepository, times(1)).findById(1L);
        verify(mockUserRepository, times(1)).findById(2L);
    }
}

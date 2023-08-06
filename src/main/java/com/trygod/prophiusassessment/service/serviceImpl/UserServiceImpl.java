package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.config.jwt.JwtUtil;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.request.AuthenticateUserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.repository.UserRepository;
import com.trygod.prophiusassessment.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService<UserDto, UserData, UserResponse> {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse create(UserDto request) {
        UserData userData = new UserData();
        BeanUtils.copyProperties(request, userData);
        userData.setPassword(passwordEncoder.encode(request.getPassword()));
        userData = userRepository.save(userData);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userData, userResponse);
        return userResponse;
    }

    @Override
    public String authenticateUser(AuthenticateUserDto request) {
        log.info("AUTH REQUEST {}", request);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Login failed");
        }
        log.info("AUTH REQUEST {}", request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(request.getEmail());
    }

    @Override
    public UserResponse update(Long id, UserDto request) {
        UserData userData = findById(id);
        BeanUtils.copyProperties(request, userData);
        userData = userRepository.save(userData);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userData, userResponse);
        return userResponse;
    }

    @Override
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(UserData.class, id);
        }
    }

    @Override
    public UserData findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(UserData.class, id));
    }

    @Override
    public MessageResponse<Page<UserResponse>> search(String keyWord, Pageable pageable) {
        Page<UserData> userDataPage = userRepository.findAllByUsernameContainingIgnoreCase(keyWord, pageable);
        Page<UserResponse> userResponsePage = userDataPage.map(userData -> {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(userData, userResponse);
            return userResponse;
        });
        return MessageResponse.response(userResponsePage);
    }

    @Override
    public UserResponse findByUsername(String username) {
        UserData userData = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(UserData.class, username));
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userData, userResponse);
        return userResponse;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void followUser(Long followerId, Long followeeId){
        UserData follower = findById(followerId);
        UserData followee = findById(followeeId);

        follower.getFollowers().add(followee);
        followee.getFollowing().add(follower);
        userRepository.save(follower);
        userRepository.save(followee);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        UserData follower = findById(followerId);
        UserData followee = findById(followeeId);

        followee.getFollowing().remove(follower);
        follower.getFollowers().remove(followee);
        userRepository.save(followee);
        userRepository.save(follower);
    }

}

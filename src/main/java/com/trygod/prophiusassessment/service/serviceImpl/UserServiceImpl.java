package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.repository.UserRepository;
import com.trygod.prophiusassessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService<UserDto, UserData> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public MessageResponse<UserDto> create(UserDto request) {
        UserData userData = new UserData();
        BeanUtils.copyProperties(request, userData);
        userData.setPassword(passwordEncoder.encode(request.getPassword()));
        userData = userRepository.save(userData);
        BeanUtils.copyProperties(userData, request);
        return messageResponse(request);
    }

    @Override
    public MessageResponse<UserDto> update(Long id, UserDto request) {
        UserData userData = findById(id);
        BeanUtils.copyProperties(request, userData);
        userData = userRepository.save(userData);
        BeanUtils.copyProperties(userData, request);
        return messageResponse(request);
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
    public MessageResponse<Page<UserDto>> search(String search, PageRequest pageRequest) {
//        Page<UserDto> userDtoPage = userRepository.findAll(new UserData().buildPredicate(search), pageRequest)
//                .map(userData -> {
//                    UserDto userDto = new UserDto();
//                    BeanUtils.copyProperties(userData, userDto);
//                    return userDto;
//                });
//        return messageResponse(userDtoPage);
        return null;
    }

    @Override
    public MessageResponse<UserDto> findByUsername(String username) {
        UserData userData = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(UserData.class, username));
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userData, userDto);
        return messageResponse(userDto);
    }

    @Override
    public void followUser(Long followerId, Long followeeId){
        UserData follower = findById(followerId);
        UserData followee = findById(followeeId);

        follower.getFollowing().add(followee);
        userRepository.save(follower);
    }

    @Override
    public void unfollowUser(Long followerId, Long followeeId) {
        UserData follower = findById(followerId);
        UserData followee = findById(followeeId);

        follower.getFollowing().remove(followee);
        userRepository.save(follower);
    }


    private <T> MessageResponse<T> messageResponse(T data) {
        MessageResponse<T> response = new MessageResponse<>();
        response.setResult(data);
        return response;
    }
}

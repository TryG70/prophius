package com.trygod.prophiusassessment.service.serviceImpl;

import com.querydsl.core.types.Predicate;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.repository.UserRepository;
import com.trygod.prophiusassessment.service.BaseEntityService;
import com.trygod.prophiusassessment.service.PageableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements BaseEntityService<UserDto, UserData>, PageableService<UserDto> {

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
        Page<UserDto> userDtoPage = userRepository.findAll(new UserData().buildPredicate(search), pageRequest)
                .map(userData -> {
                    UserDto userDto = new UserDto();
                    BeanUtils.copyProperties(userData, userDto);
                    return userDto;
                });
        return messageResponse(userDtoPage);
    }

    @Override
    public <U> MessageResponse<Page<U>> findAll(Predicate predicate, Pageable pageable, Class<U> type) {
        if(type == UserData.class) {
            return messageResponse((Page<U>) userRepository.findAll(predicate, pageable));
        } else {
            return messageResponse(Page.empty());
        }
    }

    public MessageResponse<UserDto> findByUsername(String username) {
        UserData userData = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(UserData.class, username));
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userData, userDto);
        return messageResponse(userDto);
    }

    public void followUser(Long followerId, Long followeeId){
        UserData follower = findById(followerId);
        UserData followee = findById(followeeId);

        follower.getFollowing().add(followee);
        userRepository.save(follower);
    }

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

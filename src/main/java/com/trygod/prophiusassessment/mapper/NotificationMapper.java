package com.trygod.prophiusassessment.mapper;

import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.NotificationResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationMapper {

    private final UserService<UserDto, UserData, UserResponse> userService;

    public NotificationResponse toDTO(NotificationData data) {
        NotificationResponse notificationResponse = new NotificationResponse();
        BeanUtils.copyProperties(data, notificationResponse);
        notificationResponse.setUserId(data.getUser().getId());
        return notificationResponse;
    }
}

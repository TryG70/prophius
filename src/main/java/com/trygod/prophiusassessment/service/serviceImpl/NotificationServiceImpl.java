package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.NotificationResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.repository.NotificationRepository;
import com.trygod.prophiusassessment.repository.UserRepository;
import com.trygod.prophiusassessment.service.NotificationService;
import com.trygod.prophiusassessment.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService<NotificationResponse, NotificationData> {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final UserService<UserDto, UserData, UserResponse> userService;

    @Override
    public void notifyUser(Long userId, String message) {
        UserData userData = userService.findById(userId);
        NotificationData notificationData = new NotificationData();
        notificationData.setMessage(message);
        notificationData.setUser(userData);
        notificationRepository.save(notificationData);
        userData.getNotifications().add(notificationData);
        userRepository.save(userData);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NotificationData findNotification(Long notificationId) {
        NotificationData notificationData = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(NotificationData.class, notificationId));

        markAsRead(notificationData);
        return notificationData;
    }

    @Override
    public void markAsRead(NotificationData notificationData) {
        notificationData.setRead(true);
        notificationRepository.save(notificationData);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<NotificationData> notificationDataList = notificationRepository.findAllByUser_Id(userId);
        notificationDataList.forEach(notificationData -> notificationData.setRead(true));
        notificationRepository.saveAll(notificationDataList);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteAllByUser_Id(userId);
    }

    @Override
    public MessageResponse<Page<NotificationResponse>> findUserNotifications(Long userId, Pageable pageable) {

        Page<NotificationData> notificationDataPage = notificationRepository.findAllByUser_IdOrderByCreatedDateDesc(userId, pageable);

        Page<NotificationResponse> notificationResponsePage = notificationDataPage.map(notificationData -> {
            NotificationResponse notificationResponse = new NotificationResponse();
            BeanUtils.copyProperties(notificationData, notificationResponse);
            return notificationResponse;
        });

        return MessageResponse.response(notificationResponsePage);
    }


}

package com.trygod.prophiusassessment.service.serviceImpl;

import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.exception.NotFoundException;
import com.trygod.prophiusassessment.repository.NotificationRepository;
import com.trygod.prophiusassessment.service.NotificationService;
import com.trygod.prophiusassessment.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserService<UserDto, UserData> userService;

    @Override
    public void notifyUser(Long userId, String message) {
        UserData userData = userService.findById(userId);
        NotificationData notificationData = new NotificationData();
        notificationData.setMessage(message);
        notificationData.setUser(userData);
        notificationRepository.save(notificationData);
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
}

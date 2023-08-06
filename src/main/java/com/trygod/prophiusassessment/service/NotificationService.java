package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService<V, U> {

    void notifyUser(Long userId, String message);

    U findNotification(Long notificationId);

    void markAsRead(NotificationData notificationData);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId);

    void deleteAllNotifications(Long userId);

    MessageResponse<Page<V>> findUserNotifications(Long userId, Pageable pageable);
}

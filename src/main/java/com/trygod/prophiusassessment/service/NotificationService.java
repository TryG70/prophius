package com.trygod.prophiusassessment.service;

import com.trygod.prophiusassessment.data.NotificationData;

public interface NotificationService {

    void notifyUser(Long userId, String message);

    NotificationData findNotification(Long notificationId);

    void markAsRead(NotificationData notificationData);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId);

    void deleteAllNotifications(Long userId);
}

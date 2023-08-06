package com.trygod.prophiusassessment.api;

import com.trygod.prophiusassessment.data.NotificationData;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.NotificationResponse;
import com.trygod.prophiusassessment.mapper.NotificationMapper;
import com.trygod.prophiusassessment.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Endpoints for managing notifications")
public class NotificationApi {

    private final NotificationService<NotificationResponse, NotificationData> notificationService;

    private final NotificationMapper notificationMapper;

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get a notification by ID")
    public ResponseEntity<MessageResponse<NotificationResponse>> getNotificationById(
            @PathVariable @Parameter(description = "Notification ID", required = true) Long notificationId
    ) {
        NotificationData notificationData = notificationService.findNotification(notificationId);
        NotificationResponse notificationResponse = notificationMapper.toDTO(notificationData);
        return ResponseEntity.ok(MessageResponse.response(notificationResponse));
    }

    @GetMapping(value = "/user/{userId}/page", params = {"page", "size"})
    @Operation(summary = "Get pageable notifications for a user")
    public ResponseEntity<MessageResponse<Page<NotificationResponse>>> getAllNotificationsByUserId(
            @PathVariable @Parameter(description = "User ID", required = true) Long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.findUserNotifications(userId, pageable));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable @Parameter(description = "Notification ID", required = true) Long notificationId
    ) {
        NotificationData notificationData = notificationService.findNotification(notificationId);
        notificationService.markAsRead(notificationData);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/{userId}/read")
    @Operation(summary = "Mark all notifications for a user as read")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @PathVariable @Parameter(description = "User ID", required = true) Long userId
    ) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete a notification by ID")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable @Parameter(description = "Notification ID", required = true) Long notificationId
    ) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete all notifications for a user")
    public ResponseEntity<Void> deleteAllNotifications(
            @PathVariable @Parameter(description = "User ID", required = true) Long userId
    ) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.noContent().build();
    }
}

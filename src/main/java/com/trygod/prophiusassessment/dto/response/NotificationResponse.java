package com.trygod.prophiusassessment.dto.response;

import lombok.Data;

@Data
public class NotificationResponse {

    private Long id;

    private String message;

    private boolean isRead;

    private Long userId;
}

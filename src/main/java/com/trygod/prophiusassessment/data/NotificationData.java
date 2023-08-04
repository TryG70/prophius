package com.trygod.prophiusassessment.data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "NOTIFICATIONS")
public class NotificationData extends BaseEntityData {

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "IS_READ")
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID", nullable = false)
    private UserData user;
}

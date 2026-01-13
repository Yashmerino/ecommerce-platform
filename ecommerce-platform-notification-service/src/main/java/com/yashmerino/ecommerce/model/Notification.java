package com.yashmerino.ecommerce.model;

import com.yashmerino.ecommerce.model.base.BaseEntity;
import com.yashmerino.ecommerce.utils.ContactType;
import com.yashmerino.ecommerce.utils.NotificationStatus;
import com.yashmerino.ecommerce.utils.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity for notification.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "notifications")
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType contactType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private int retryCount = 0;

    @Column(length = 500)
    private String lastError;

    private LocalDateTime sentAt;
}
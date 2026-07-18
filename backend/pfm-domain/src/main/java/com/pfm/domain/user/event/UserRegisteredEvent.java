package com.pfm.domain.user.event;

import com.pfm.domain.shared.event.DomainEvent;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class UserRegisteredEvent implements DomainEvent {
    UUID userId;
    String email;
    LocalDateTime occurredOn;

    public UserRegisteredEvent(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
        this.occurredOn = LocalDateTime.now();
    }
}
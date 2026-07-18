package com.pfm.domain.user.event;

import com.pfm.domain.shared.event.DomainEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@AllArgsConstructor
public class UserRegisteredEvent implements DomainEvent {
    UUID userId;
    String email;

    @Getter(AccessLevel.NONE)
    LocalDateTime occurredOn;

    public UserRegisteredEvent(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
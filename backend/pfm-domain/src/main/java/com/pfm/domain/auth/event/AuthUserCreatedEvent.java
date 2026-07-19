package com.pfm.domain.auth.event;

import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.domain.shared.event.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthUserCreatedEvent implements DomainEvent {
    private final AuthUserId userId;
    private final String email;
    private final LocalDateTime occurredOn;

    public AuthUserCreatedEvent(String userId, String email) {
        this.userId = AuthUserId.from(userId);
        this.email = email;
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}

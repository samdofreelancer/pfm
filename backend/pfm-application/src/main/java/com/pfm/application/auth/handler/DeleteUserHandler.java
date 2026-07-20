package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.DeleteUserCommand;
import com.pfm.application.common.CommandHandler;
import com.pfm.domain.auth.model.AuthUserId;
import com.pfm.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserHandler implements CommandHandler<DeleteUserCommand, Void> {

    private final AuthRepository authRepository;

    @Override
    @Transactional
    public Void handle(DeleteUserCommand command) {
        var authUser = authRepository.findByEmail(command.getEmail());
        if (authUser.isPresent()) {
            authRepository.delete(authUser.get().getId());
        }
        return null;
    }
}

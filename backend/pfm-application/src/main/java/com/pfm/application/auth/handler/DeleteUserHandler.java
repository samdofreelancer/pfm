package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.DeleteUserCommand;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserHandler implements CommandHandler<DeleteUserCommand, Void> {

    private final AuthRepository authRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public Void handle(DeleteUserCommand command) {
        String userEmail = currentUserProvider.currentUserEmail();
        var authUser = authRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", 404));
        authUser.delete();
        authRepository.save(authUser);
        return null;
    }
}

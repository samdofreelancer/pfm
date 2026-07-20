package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.ChangePasswordCommand;
import com.pfm.application.common.CommandHandler;
import com.pfm.common.exception.BusinessException;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePasswordHandler implements CommandHandler<ChangePasswordCommand, Void> {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Void handle(ChangePasswordCommand command) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        AuthUser authUser = authRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", 404));

        if (!passwordEncoder.matches(command.getCurrentPassword(), authUser.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Current password is incorrect", 400);
        }

        String encodedNewPassword = passwordEncoder.encode(command.getNewPassword());
        authUser.updatePassword(encodedNewPassword);
        authRepository.save(authUser);

        return null;
    }
}
package com.pfm.application.auth.handler;

import com.pfm.application.auth.command.UpdateProfileCommand;
import com.pfm.application.auth.dto.ProfileResponse;
import com.pfm.application.auth.mapper.AuthMapper;
import com.pfm.application.common.CommandHandler;
import com.pfm.domain.auth.model.AuthUser;
import com.pfm.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProfileHandler implements CommandHandler<UpdateProfileCommand, ProfileResponse> {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;

    @Override
    @Transactional
    public ProfileResponse handle(UpdateProfileCommand command) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        AuthUser authUser = authRepository.findByEmail(userEmail)
            .orElseThrow(() -> new com.pfm.common.exception.BusinessException("USER_NOT_FOUND", "User not found", 404));

        authUser.updateFullName(command.getFullName());
        AuthUser savedUser = authRepository.save(authUser);

        return authMapper.toProfileResponse(savedUser);
    }
}
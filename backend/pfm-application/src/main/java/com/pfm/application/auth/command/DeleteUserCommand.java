package com.pfm.application.auth.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeleteUserCommand {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email;
}
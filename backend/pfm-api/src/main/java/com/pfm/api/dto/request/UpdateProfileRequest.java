package com.pfm.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UpdateProfileRequest {
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    String fullName;
}
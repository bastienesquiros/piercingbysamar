package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetupRequest(
        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 12, message = "Le mot de passe doit faire au moins 12 caractères")
        String password
) {}

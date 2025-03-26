package com.example.TaskManagementSystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AuthenticationDTO(
        @Schema(example = "user@example.com", description = "User's email")
        @NotEmpty(message = "Email не должен быть пустым")
        @Size(min = 4, max = 100, message = "Email должен быть от 4 до 100 символов")
        @Email(message = "Введите корректный email")
        String email,
        @Schema(example = "passworD123", description = "User's password")
        String password) {
}

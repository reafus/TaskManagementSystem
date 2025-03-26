package com.example.TaskManagementSystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @Schema(example = "Bobby", description = "User's name")
        @NotEmpty
        @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
        String username,

        @Schema(example = "user@example.com", description = "User's email")
        @Email(message = "Введите корректный email")
        @NotEmpty
        @Size(min = 4, max = 100)
        String email,

        @Schema(example = "passworD123", description = "User's password")
        String password
        ) {
}

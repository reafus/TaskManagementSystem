package com.example.TaskManagementSystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TaskDTO(
        @Schema(description = "Title of the task", example = "Implement user auth")
        @NotEmpty(message = "Title cannot be empty")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,
        @Schema(description = "Description for the task", example = "Develop the authentication module for user login...")
        String description,
        @Schema(description = "Status of the task", example = "IN_PROGRESS",
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"})
        @NotEmpty(message = "Status cannot be empty")
        String status,
        @Schema(description = "Priority of the task", example = "HIGH",
                allowableValues = {"LOW", "MEDIUM", "HIGH"})
        @NotEmpty(message = "Priority cannot be empty")
        String priority,
        @Schema(description = "Author of the task", example = "Admin")
        @NotEmpty(message = "Author cannot be empty")
        String author,
        @Schema(description = "List of assignees for the task")
        List<String> assignees,
        @Schema(description = "Comments related to the task")
        List<CommentDTO> comments

) {
}

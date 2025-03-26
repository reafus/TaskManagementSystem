package com.example.TaskManagementSystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CommentDTO(
        @Schema(description = "Comment text", example = "Need to help for this task.")
        @NotEmpty(message = "Comment text cannot be empty")
        @Size(min = 2, max = 500, message = "Comment must be between 2 and 500 characters")
        String text,

        @Schema(description = "Author of the comment", example = "Bobby")
        String author
) {
}

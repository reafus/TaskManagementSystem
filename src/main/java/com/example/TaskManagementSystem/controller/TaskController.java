package com.example.TaskManagementSystem.controller;

import com.example.TaskManagementSystem.dto.CommentDTO;
import com.example.TaskManagementSystem.dto.TaskDTO;
import com.example.TaskManagementSystem.exceptions.EntityNotFoundException;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.security.JwtUtil;
import com.example.TaskManagementSystem.services.TaskService;
import com.example.TaskManagementSystem.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "TaskController", description = "Task endpoints for USER")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public TaskController(TaskService taskService, UserService userService, JwtUtil jwtUtil) {
        this.taskService = taskService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Get User tasks",
            description = "Retrieve all tasks for the current user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InvalidRequest",
                                            value = "{ \"error\": \"Invalid token\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InternalError",
                                            value = "{ \"error\": \"Internal server error\" }"
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasksForUser(
            @RequestHeader("Authorization") String token) {

        User user = getCurrentUser(token);
        return ResponseEntity.ok(taskService.getTasksForUser(user.getUsername()));
    }

    @Operation(
            summary = "Get task by ID",
            description = "Retrieve a specific task by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "AccessDenied",
                                            value = "{ \"error\": \"Access denied\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "NotFound",
                                            value = "{ \"error\": \"Task not found\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InternalError",
                                            value = "{ \"error\": \"Internal server error\" }"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id,
                                               @RequestHeader("Authorization") String token) {

        User user = getCurrentUser(token);

        taskService.checkTaskAccess(id, user.getUsername());

        return ResponseEntity.ok(taskService.getById(id));
    }

    @Operation(
            summary = "Update task status",
            description = "Update the status of a specific task",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid status",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InvalidStatus",
                                            value = "{ \"error\": \"Invalid status value\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "AccessDenied",
                                            value = "{ \"error\": \"Access denied\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "NotFound",
                                            value = "{ \"error\": \"Task not found\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InternalError",
                                            value = "{ \"error\": \"Internal server error\" }"
                                    )
                            )
                    )
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("Authorization") String token
    ) {


        User user = getCurrentUser(token);

        taskService.checkTaskAccess(id, user.getUsername());

        return ResponseEntity.ok(taskService.changeStatus(id, status));
    }

    @Operation(
            summary = "Add comment to task",
            description = "Add a new comment to a specific task",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment added successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InvalidInput",
                                            value = "{ \"error\": \"Comment text is required\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "AccessDenied",
                                            value = "{ \"error\": \"Access denied\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "NotFound",
                                            value = "{ \"error\": \"Task not found\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InternalError",
                                            value = "{ \"error\": \"Internal server error\" }"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDTO commentDTO,
            @RequestHeader("Authorization") String token
    ) {

        User user = getCurrentUser(token);

        return ResponseEntity.ok(taskService.addComment(id, commentDTO, user.getUsername()));
    }

    private User getCurrentUser(String token) {
        String email = jwtUtil.validateTokenAndRetrieveClaim(token.substring(7));
        return userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}

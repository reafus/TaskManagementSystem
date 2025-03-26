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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/admin")
@Tag(name = "AdminTaskController", description = "Task endpoints for admin")
public class AdminTaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminTaskController(TaskService taskService, UserService userService, JwtUtil jwtUtil) {
        this.taskService = taskService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Get all tasks",
            description = "Retrieve all tasks (admin only)",
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
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(
            summary = "Create new task",
            description = "Create new task (admin only)",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Task created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "ValidationError",
                                            value = "{ \"error\": \"Title is required\" }"
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
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(taskDTO));
    }

    @Operation(
            summary = "Update task",
            description = "Update existing task by ID (admin only)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "ValidationError",
                                            value = "{ \"error\": \"Invalid input\" }"
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
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO
    ) {
        return ResponseEntity.ok(taskService.update(id, taskDTO));
    }

    @Operation(
            summary = "Delete task",
            description = "Delete task by ID (admin only)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Change task status",
            description = "Update task status by ID (admin only)",
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
    public ResponseEntity<TaskDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(taskService.changeStatus(id, status));
    }

    @Operation(
            summary = "Change task priority",
            description = "Update task priority by ID (admin only)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Priority updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid priority",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InvalidPriority",
                                            value = "{ \"error\": \"Invalid priority value\" }"
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
    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskDTO> changePriority(
            @PathVariable Long id,
            @RequestParam String priority
    ) {
        return ResponseEntity.ok(taskService.changePriority(id, priority));
    }

    @Operation(
            summary = "Assign user to task",
            description = "Assign user to task by username (admin only)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User assigned successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid username",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "InvalidUser",
                                            value = "{ \"error\": \"User not found\" }"
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
    @PostMapping("/{id}/assign")
    public ResponseEntity<TaskDTO> assignUserToTask(
            @PathVariable Long id,
            @RequestParam String username
    ) {
        return ResponseEntity.ok(taskService.assignUser(id, username));
    }

    @Operation(
            summary = "Add comment to task",
            description = "Add comment to task (admin only)",
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
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "ValidationError",
                                            value = "{ \"error\": \"Comment text is required\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "Unauthorized",
                                            value = "{ \"error\": \"Invalid token\" }"
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
        String email = jwtUtil.validateTokenAndRetrieveClaim(token.substring(7));
        User admin =  userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(taskService.addComment(id, commentDTO, admin.getUsername()));
    }

}

package com.example.TaskManagementSystem.controller;

import com.example.TaskManagementSystem.dto.CommentDTO;
import com.example.TaskManagementSystem.dto.TaskDTO;
import com.example.TaskManagementSystem.exceptions.EntityNotFoundException;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.security.JwtUtil;
import com.example.TaskManagementSystem.services.TaskService;
import com.example.TaskManagementSystem.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TaskController taskController;

    @Test
    void getTasksForUser_ShouldReturnUserTasks() {
        // Arrange
        String token = "Bearer validToken";
        User user = new User();
        user.setUsername("testUser");
        List<TaskDTO> expectedTasks = List.of(new TaskDTO("testTask1", "123", "PENDING", "HIGH", "admin", Collections.singletonList("testUser"), null),
                new TaskDTO("testTask2", "123", "COMPLETED", "HIGH", "admin", null, null));

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn("user@mail.com");
        when(userService.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(taskService.getTasksForUser("testUser")).thenReturn(expectedTasks);

        // Act
        ResponseEntity<List<TaskDTO>> response = taskController.getTasksForUser(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTasks, response.getBody());
        verify(taskService).getTasksForUser("testUser");
    }

    @Test
    void getTaskById_ShouldReturnTaskWhenAuthorized() {
        // Arrange
        Long taskId = 1L;
        String token = "Bearer validToken";
        User user = new User();
        user.setUsername("testUser");
        TaskDTO expectedTask = new TaskDTO("testTask1", "123", "PENDING", "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn("user@mail.com");
        when(userService.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(taskService.getById(taskId)).thenReturn(expectedTask);

        // Act
        ResponseEntity<TaskDTO> response = taskController.getTaskById(taskId, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTask, response.getBody());
        verify(taskService).checkTaskAccess(taskId, "testUser");
    }

    @Test
    void getTaskById_ShouldThrowAccessDenied() {
        // Arrange
        Long taskId = 1L;
        String token = "Bearer validToken";
        User user = new User();
        user.setUsername("testUser");

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn("user@mail.com");
        when(userService.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        doThrow(new AccessDeniedException("Access denied"))
                .when(taskService).checkTaskAccess(taskId, "testUser");

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                taskController.getTaskById(taskId, token)
        );
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatusWhenAuthorized() {
        // Arrange
        Long taskId = 1L;
        String status = "DONE";
        String token = "Bearer validToken";
        User user = new User();
        user.setUsername("testUser");
        TaskDTO expectedTask = new TaskDTO("testTask1", "123", "PENDING", "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn("user@mail.com");
        when(userService.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(taskService.changeStatus(taskId, status)).thenReturn(expectedTask);

        // Act
        ResponseEntity<TaskDTO> response = taskController.updateTaskStatus(taskId, status, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTask, response.getBody());
        verify(taskService).checkTaskAccess(taskId, "testUser");
    }

    @Test
    void addComment_ShouldAddCommentWithUsername() {
        // Arrange
        Long taskId = 1L;
        String token = "Bearer validToken";
        CommentDTO commentDTO = new CommentDTO("text", "testUser");
        User user = new User();
        user.setUsername("testUser");
        CommentDTO expectedComment = new CommentDTO("text", "testUser");

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn("user@mail.com");
        when(userService.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(taskService.addComment(eq(taskId), any(CommentDTO.class), eq("testUser")))
                .thenReturn(expectedComment);

        // Act
        ResponseEntity<CommentDTO> response = taskController.addComment(taskId, commentDTO, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedComment, response.getBody());
        verify(taskService).addComment(taskId, commentDTO, "testUser");
    }

    @Test
    void getCurrentUser_ShouldThrowWhenUserNotFound() {
        // Arrange
        String token = "Bearer validToken";
        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn("user@mail.com");
        when(userService.findByEmail("user@mail.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                taskController.getTasksForUser(token)
        );
    }
}

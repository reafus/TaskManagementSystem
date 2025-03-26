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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminTaskController adminTaskController;

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Arrange
        TaskDTO task1 = new TaskDTO("testTask1", "123", "COMPLETED", "HIGH", "admin", null, null);
        TaskDTO task2 = new TaskDTO("testTask2", "123", "PENDING", "LOW", "admin", null, null);
        List<TaskDTO> expectedTasks = List.of(task1, task2);
        when(taskService.getAllTasks()).thenReturn(expectedTasks);

        // Act
        ResponseEntity<List<TaskDTO>> response = adminTaskController.getAllTasks();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTasks, response.getBody());
        verify(taskService).getAllTasks();
    }
    @Test
    void createTask_ShouldReturnCreatedTask() {
        // Arrange
        TaskDTO inputTask = new TaskDTO("testTask1", "123", "COMPLETED", "HIGH", "admin", null, null);
        TaskDTO createdTask = new TaskDTO("testTask2", "123", "PENDING", "LOW", "admin", null, null);
        when(taskService.create(inputTask)).thenReturn(createdTask);

        // Act
        ResponseEntity<TaskDTO> response = adminTaskController.createTask(inputTask);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdTask, response.getBody());
        verify(taskService).create(inputTask);
    }
    @Test
    void updateTask_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        TaskDTO inputTask = new TaskDTO("testTask1", "123", "COMPLETED", "HIGH", "admin", null, null);
        TaskDTO updatedTask = new TaskDTO("testTask2", "123", "PENDING", "LOW", "admin", null, null);
        when(taskService.update(taskId, inputTask)).thenReturn(updatedTask);

        // Act
        ResponseEntity<TaskDTO> response = adminTaskController.updateTask(taskId, inputTask);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTask, response.getBody());
        verify(taskService).update(taskId, inputTask);
    }
    @Test
    void deleteTask_ShouldReturnNoContent() {
        // Arrange
        Long taskId = 1L;

        // Act
        ResponseEntity<Void> response = adminTaskController.deleteTask(taskId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).delete(taskId);
    }

    @Test
    void changeStatus_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        String newStatus = "IN_PROGRESS";
        TaskDTO updatedTask = new TaskDTO("testTask1", "123", "COMPLETED", "HIGH", "admin", null, null);
        when(taskService.changeStatus(taskId, newStatus)).thenReturn(updatedTask);

        // Act
        ResponseEntity<TaskDTO> response = adminTaskController.changeStatus(taskId, newStatus);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTask, response.getBody());
        verify(taskService).changeStatus(taskId, newStatus);
    }

    @Test
    void changePriority_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        String newPriority = "HIGH";
        TaskDTO updatedTask = new TaskDTO("testTask1", "123", "COMPLETED", "HIGH", "admin", null, null);
        when(taskService.changePriority(taskId, newPriority)).thenReturn(updatedTask);

        // Act
        ResponseEntity<TaskDTO> response = adminTaskController.changePriority(taskId, newPriority);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTask, response.getBody());
        verify(taskService).changePriority(taskId, newPriority);
    }

    @Test
    void assignUserToTask_ShouldReturnUpdatedTask() {
        // Arrange
        Long taskId = 1L;
        String username = "user";
        TaskDTO updatedTask = new TaskDTO("testTask1", "123", "COMPLETED", "HIGH", "admin", null, null);
        when(taskService.assignUser(taskId, username)).thenReturn(updatedTask);

        // Act
        ResponseEntity<TaskDTO> response = adminTaskController.assignUserToTask(taskId, username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTask, response.getBody());
        verify(taskService).assignUser(taskId, username);
    }

    @Test
    void addComment_ShouldReturnComment() {
        // Arrange
        Long taskId = 1L;
        CommentDTO commentDTO = new CommentDTO("text", "user");
        String token = "Bearer validToken";
        String email = "admin@example.com";
        User admin = new User();
        admin.setUsername("admin");
        CommentDTO expectedComment = new CommentDTO("text", "user");

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.of(admin));
        when(taskService.addComment(eq(taskId), any(CommentDTO.class), eq("admin")))
                .thenReturn(expectedComment);

        // Act
        ResponseEntity<CommentDTO> response = adminTaskController.addComment(
                taskId,
                commentDTO,
                token
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedComment, response.getBody());
        verify(taskService).addComment(taskId, commentDTO, "admin");
    }

    @Test
    void addComment_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        Long taskId = 1L;
        CommentDTO commentDTO = new CommentDTO("text", "user");
        String token = "Bearer validToken";
        String email = "admin@example.com";

        when(jwtUtil.validateTokenAndRetrieveClaim("validToken")).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                adminTaskController.addComment(taskId, commentDTO, token)
        );
    }
}

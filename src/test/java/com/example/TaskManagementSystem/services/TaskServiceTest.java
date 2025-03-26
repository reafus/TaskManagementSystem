package com.example.TaskManagementSystem.services;

import com.example.TaskManagementSystem.dto.CommentDTO;
import com.example.TaskManagementSystem.dto.TaskDTO;
import com.example.TaskManagementSystem.exceptions.EntityNotFoundException;
import com.example.TaskManagementSystem.mappers.CommentMapper;
import com.example.TaskManagementSystem.mappers.TaskMapper;
import com.example.TaskManagementSystem.models.Comment;
import com.example.TaskManagementSystem.models.Task;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.models.enums.TaskPriority;
import com.example.TaskManagementSystem.models.enums.TaskStatus;
import com.example.TaskManagementSystem.repositories.CommentRepository;
import com.example.TaskManagementSystem.repositories.TaskRepository;
import com.example.TaskManagementSystem.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void create_ShouldSaveAndReturnTaskDTO() {
        // Arrange
        TaskDTO inputDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);
        Task entity = new Task();
        Task savedEntity = new Task();
        TaskDTO expectedDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskMapper.toEntity(inputDTO)).thenReturn(entity);
        when(taskRepository.save(entity)).thenReturn(savedEntity);
        when(taskMapper.toDTO(savedEntity)).thenReturn(expectedDTO);

        // Act
        TaskDTO result = taskService.create(inputDTO);

        // Assert
        assertEquals(expectedDTO, result);
        verify(taskRepository).save(entity);
    }

    @Test
    void getAllTasks_ShouldReturnAllMappedTasks() {
        // Arrange
        Task task1 = new Task();
        Task task2 = new Task();
        TaskDTO dto1 = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);
        TaskDTO dto2 = new TaskDTO("testTask2", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        when(taskMapper.toDTO(task1)).thenReturn(dto1);
        when(taskMapper.toDTO(task2)).thenReturn(dto2);

        // Act
        List<TaskDTO> result = taskService.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
    }

    @Test
    void getById_ShouldReturnTaskWhenExists() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        TaskDTO expectedDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(expectedDTO);

        // Act
        TaskDTO result = taskService.getById(taskId);

        // Assert
        assertEquals(expectedDTO, result);
    }

    @Test
    void getById_ShouldThrowWhenTaskNotExists() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.getById(taskId));
    }

    @Test
    void update_ShouldUpdateExistingTask() {
        // Arrange
        Long taskId = 1L;
        TaskDTO inputDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);
        Task existingTask = new Task();
        Task updatedTask = new Task();
        TaskDTO expectedDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);
        when(taskMapper.toDTO(updatedTask)).thenReturn(expectedDTO);

        // Act
        TaskDTO result = taskService.update(taskId, inputDTO);

        // Assert
        assertEquals(expectedDTO, result);
        verify(taskMapper).updateTaskFromDto(inputDTO, existingTask);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // Arrange
        Long taskId = 1L;

        // Act
        taskService.delete(taskId);

        // Assert
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void changeStatus_ShouldUpdateTaskStatus() {
        // Arrange
        Long taskId = 1L;
        String status = "IN_PROGRESS";
        Task task = new Task();
        TaskDTO expectedDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(expectedDTO);

        // Act
        TaskDTO result = taskService.changeStatus(taskId, status);

        // Assert
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(expectedDTO, result);
    }

    @Test
    void changePriority_ShouldUpdateTaskPriority() {
        // Arrange
        Long taskId = 1L;
        String priority = "HIGH";
        Task task = new Task();
        TaskDTO expectedDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(expectedDTO);

        // Act
        TaskDTO result = taskService.changePriority(taskId, priority);

        // Assert
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(expectedDTO, result);
    }

    @Test
    void assignUser_ShouldAddUserToAssignees() {
        // Arrange
        Long taskId = 1L;
        String username = "user";
        Task task = new Task();
        User user = new User();
        TaskDTO expectedDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(expectedDTO);

        // Act
        TaskDTO result = taskService.assignUser(taskId, username);

        // Assert
        assertTrue(task.getAssignees().contains(user));
        assertEquals(expectedDTO, result);
    }

    @Test
    void addComment_ShouldSaveCommentWithRelations() {
        // Arrange
        Long taskId = 1L;
        String username = "author";
        CommentDTO inputDTO = new CommentDTO("text", "author");
        Comment entity = new Comment();
        Comment savedComment = new Comment();
        CommentDTO expectedDTO = new CommentDTO("text", "author");
        Task task = new Task();
        User author = new User();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(author));
        when(commentMapper.toEntity(inputDTO)).thenReturn(entity);
        when(commentRepository.save(entity)).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(expectedDTO);

        // Act
        CommentDTO result = taskService.addComment(taskId, inputDTO, username);

        // Assert
        assertEquals(expectedDTO, result);
        assertEquals(task, entity.getTask());
        assertEquals(author, entity.getAuthor());
    }

    @Test
    void getTasksForUser_ShouldReturnFilteredTasks() {
        // Arrange
        String username = "user";
        User user = new User();
        Task task1 = new Task();
        Task task2 = new Task();
        TaskDTO dto1 = new TaskDTO("testTask1", "123", "PENDING", "HIGH", "admin", Collections.singletonList("user"), null);
        TaskDTO dto2 = new TaskDTO("testTask2", "123", "PENDING", "LOW", "admin", Collections.singletonList("user"), null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskRepository.findByAuthorOrAssigneesContains(user)).thenReturn(List.of(task1, task2));
        when(taskMapper.toDTO(task1)).thenReturn(dto1);
        when(taskMapper.toDTO(task2)).thenReturn(dto2);

        // Act
        List<TaskDTO> result = taskService.getTasksForUser(username);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
    }

    @Test
    void checkTaskAccess_ShouldThrowWhenNoAccess() {
        // Arrange
        Long taskId = 1L;
        String username = "user";
        TaskDTO taskDTO = new TaskDTO("testTask1", "123", "PENDING",
                "HIGH", "admin", Collections.singletonList("testUser"), null
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new Task()));
        when(taskMapper.toDTO(any())).thenReturn(taskDTO);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> taskService.checkTaskAccess(taskId, username));
    }
}

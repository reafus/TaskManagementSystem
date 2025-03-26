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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;

    public TaskService(UserRepository userRepository, TaskRepository taskRepository, CommentRepository commentRepository, TaskMapper taskMapper, CommentMapper commentMapper) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public TaskDTO create(TaskDTO taskDTO) {
        Task convertedTask =taskMapper.toEntity(taskDTO);
        convertedTask = taskRepository.save(convertedTask);
        return taskMapper.toDTO(convertedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDTO getById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Transactional
    public TaskDTO update(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskMapper.updateTaskFromDto(taskDTO, task);
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @Transactional
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional
    public TaskDTO changeStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setStatus(TaskStatus.valueOf(status.toUpperCase()));

        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO changePriority(Long id, String priority) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setPriority(TaskPriority.valueOf(priority.toUpperCase()));
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO assignUser(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        task.getAssignees().add(user);
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public CommentDTO addComment(Long taskId, CommentDTO commentDTO, String authorUsername) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setTask(task);
        comment.setAuthor(author);
        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return taskRepository.findByAuthorOrAssigneesContains(user).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public void checkTaskAccess(Long taskId, String username) {
        TaskDTO task = getById(taskId);
        if (!task.author().equals(username) && !task.assignees().contains(username)) {
            throw new AccessDeniedException("You can't modify this task");
        }
    }



}

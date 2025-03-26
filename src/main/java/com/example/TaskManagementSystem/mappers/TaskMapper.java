package com.example.TaskManagementSystem.mappers;


import com.example.TaskManagementSystem.dto.TaskDTO;
import com.example.TaskManagementSystem.models.Task;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.models.enums.TaskPriority;
import com.example.TaskManagementSystem.models.enums.TaskStatus;
import com.example.TaskManagementSystem.util.UserFinder;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, UserMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class TaskMapper {

    @Autowired
    protected UserFinder userFinder;



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", source = "author", qualifiedByName = "taskAuthor")
    @Mapping(target = "assignees", source = "assignees", qualifiedByName = "mapAssignees")
    public abstract Task toEntity(TaskDTO dto);

    @Mapping(target = "author", source = "author.username")
    @Mapping(target = "assignees", source = "assignees", qualifiedByName = "mapAssigneesInverse")
    @InheritInverseConfiguration(name = "toEntity")
    public abstract TaskDTO toDTO(Task entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "priority", source = "priority")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "author", source = "author", qualifiedByName = "taskAuthor")
    @Mapping(target = "assignees", source = "assignees", qualifiedByName = "mapAssignees")
    public abstract void updateTaskFromDto(TaskDTO dto, @MappingTarget Task task);

    protected TaskStatus mapStatus(String status) {
        return status != null ? TaskStatus.valueOf(status.toUpperCase()) : null;
    }

    protected TaskPriority mapPriority(String priority) {
        return priority != null ? TaskPriority.valueOf(priority.toUpperCase()) : null;
    }

    @Named("taskAuthor")
    protected User mapAuthor(String username) {
        return userFinder.findByUsername(username);
    }

    @Named("mapAssignees")
    protected Set<User> mapAssignees(List<String> usernames) {
        return usernames.stream()
                .map(userFinder::findByUsername)
                .collect(Collectors.toSet());
    }

    @Named("mapAssigneesInverse")
    protected List<String> mapAssignees(Set<User> users) {
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}

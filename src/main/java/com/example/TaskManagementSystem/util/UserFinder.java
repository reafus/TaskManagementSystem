package com.example.TaskManagementSystem.util;

import com.example.TaskManagementSystem.exceptions.EntityNotFoundException;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.services.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserFinder {

    private final UserService userService;

    public UserFinder(UserService userService) {
        this.userService = userService;
    }

    public User findByUsername(String username) {
        return userService.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}

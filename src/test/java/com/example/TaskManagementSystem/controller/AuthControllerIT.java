package com.example.TaskManagementSystem.controller;

import com.example.TaskManagementSystem.dto.AuthenticationDTO;
import com.example.TaskManagementSystem.dto.UserDTO;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void testRegistration_Success() throws Exception {
        UserDTO userDTO = new UserDTO( "testUser","test@example.com", "password123");

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token").exists());

        User registeredUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getEmail()).isEqualTo("test@example.com");
        assertThat(passwordEncoder.matches("password123", registeredUser.getPassword())).isTrue();
    }

    @Test
    void testRegistration_InvalidEmail() throws Exception {
        UserDTO userDTO = new UserDTO("testUser", "invalid-email", "password123");

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Введите корректный email"));
    }


    @Test
    void testLogin_Success() throws Exception {
        // Register user first
        UserDTO userDTO = new UserDTO("testUser", "login@example.com", "password123");
        mockMvc.perform(post("/api/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        AuthenticationDTO authDTO = new AuthenticationDTO("login@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token").exists());
    }

    @Test
    void testLogin_WrongPassword() throws Exception {
        // Register user
        UserDTO userDTO = new UserDTO("testUser", "user@example.com", "password123");
        mockMvc.perform(post("/api/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // Try login with wrong password
        AuthenticationDTO authDTO = new AuthenticationDTO("user@example.com", "wrong-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void testLogin_NonExistentUser() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("nonexistent@example.com", "password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

}

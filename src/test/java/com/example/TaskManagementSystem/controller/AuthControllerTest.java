package com.example.TaskManagementSystem.controller;

import com.example.TaskManagementSystem.dto.AuthenticationDTO;
import com.example.TaskManagementSystem.dto.UserDTO;
import com.example.TaskManagementSystem.mappers.UserMapper;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.security.JwtUtil;
import com.example.TaskManagementSystem.services.RegistrationService;
import com.example.TaskManagementSystem.util.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RegistrationService registrationService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void performRegistration_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO("Bob","test@example.com", "password");
        User user = new User();
        user.setEmail(userDTO.email());
        user.setPassword(userDTO.password());
        String expectedToken = "test.jwt.token";

        when(userMapper.toUser(any())).thenReturn(user);
        when(jwtUtil.generateToken(any())).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt-token").value(expectedToken));

        verify(registrationService).register(user);
        verify(userValidator).validate(eq(user), any(BindingResult.class));
    }

    @Test
    void performRegistration_ValidationError() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO("ss","invalid", "pass");
        User user = new User();
        user.setEmail(userDTO.email());
        user.setPassword(userDTO.password());

        when(userMapper.toUser(any())).thenReturn(user);
        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.reject("error", "Введите корректный email");
            return null;
        }).when(userValidator).validate(any(), any());

        // When & Then
        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Введите корректный email"));
    }

    @Test
    void performLogin_Success() throws Exception {
        // Given
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");
        String expectedToken = "test.jwt.token";

        when(jwtUtil.generateToken(any())).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt-token").value(expectedToken));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @Test
    void performLogin_InvalidCredentials() throws Exception {
        // Given
        AuthenticationDTO authDTO = new AuthenticationDTO("wrong@example.com", "wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void performLogin_InternalError() throws Exception {
        // Given
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Some internal error"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal server error"));
    }

}

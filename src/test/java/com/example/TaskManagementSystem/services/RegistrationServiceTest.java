package com.example.TaskManagementSystem.services;

import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.models.enums.Role;
import com.example.TaskManagementSystem.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void register_ShouldEncodePasswordAndSetDefaultRole() {
        // Arrange
        User user = new User();
        user.setPassword("rawPassword");
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode("rawPassword")).thenReturn(encodedPassword);

        // Act
        registrationService.register(user);

        // Assert
        // Verify password encoding
        verify(passwordEncoder).encode("rawPassword");
        assertEquals(encodedPassword, user.getPassword());

        // Verify role assignment
        assertEquals(Role.ROLE_USER, user.getRole());

        // Verify repository save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertSame(user, savedUser);
    }

    @Test
    void register_ShouldHandleExistingRole() {
        // Arrange
        User user = new User();
        user.setPassword("password");
        user.setRole(Role.ROLE_ADMIN);

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        // Act
        registrationService.register(user);

        // Assert
        assertEquals(Role.ROLE_USER, user.getRole());
    }

    @Test
    void registerMethod_ShouldBeTransactional() throws NoSuchMethodException {
        // Arrange
        Method registerMethod = RegistrationService.class
                .getDeclaredMethod("register", User.class);

        // Act
        Transactional transactionalAnnotation =
                registerMethod.getAnnotation(Transactional.class);

        // Assert
        assertNotNull(transactionalAnnotation,
                "Method should have @Transactional annotation");
    }

    @Test
    void register_ShouldPropagateExceptions() {
        // Arrange
        User user = new User();
        user.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        doThrow(new RuntimeException("DB error")).when(userRepository).save(any());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> registrationService.register(user),
                "Should propagate repository exceptions");
    }
}

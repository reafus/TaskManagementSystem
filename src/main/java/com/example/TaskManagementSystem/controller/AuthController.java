package com.example.TaskManagementSystem.controller;

import com.example.TaskManagementSystem.dto.AuthenticationDTO;
import com.example.TaskManagementSystem.dto.UserDTO;
import com.example.TaskManagementSystem.mappers.UserMapper;
import com.example.TaskManagementSystem.models.User;
import com.example.TaskManagementSystem.security.JwtUtil;
import com.example.TaskManagementSystem.services.RegistrationService;
import com.example.TaskManagementSystem.util.UserValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Registration and login endpoints")
public class AuthController {

    private final RegistrationService registrationService;
    private final UserValidator userValidator;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager, RegistrationService registrationService,
                          UserValidator userValidator, JwtUtil jwtUtil, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.registrationService = registrationService;
        this.userValidator = userValidator;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Operation(
            summary = "User registration",
            description = "Register new User and get JWT token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Registration successful",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class), // Указываем тип ответа
                                    examples = @ExampleObject(
                                            name = "SuccessResponse",
                                            value = "{ \"jwt-token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "ValidationError",
                                            value = "{ \"error\": \"Email should be valid\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "InternalServerError",
                                            value = "{ \"error\": \"Internal server error\" }"
                                    )
                            )
                    )
            })
    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> performRegistration(@Valid @RequestBody UserDTO userDTO,
                                           BindingResult bindingResult) {
        User user = convertToUser(userDTO);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        registrationService.register(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user and get JWT token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "SuccessResponse",
                                            value = "{ \"jwt-token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "Invalid credentials",
                                            value = "{ \"error\": \"Invalid email or password\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "InternalServerError",
                                            value = "{ \"error\": \"Internal server error\" }"
                                    )
                            )
                    )
            })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.email(),
                        authenticationDTO.password());
        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
        String token = jwtUtil.generateToken(authenticationDTO.email());
        return ResponseEntity.ok(Map.of("jwt-token", token));
    }

    private User convertToUser(UserDTO userDTO) {
        return userMapper.toUser(userDTO);
    }
}

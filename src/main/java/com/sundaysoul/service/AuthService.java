package com.sundaysoul.service;

import com.sundaysoul.config.JwtUtil;
import com.sundaysoul.dto.*;
import com.sundaysoul.model.User;
import com.sundaysoul.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register new user
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Determine role: ADMIN if email matches specific admins or contains "admin"
        boolean isAdmin = request.getEmail().equalsIgnoreCase("sumitkumar950840@gmail.com") ||
                request.getEmail().equalsIgnoreCase("bangalsubham@gmail.com") ||
                request.getEmail().toLowerCase().contains("admin");

        User.Role role = isAdmin ? User.Role.ADMIN : User.Role.USER;

        log.info("Creating {} user: {}", role, request.getEmail());

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .bio("Adventure enthusiast! 🌍")
                .avatar("https://via.placeholder.com/150")
                .role(role) // Set role based on email
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(convertToUserDTO(user))
                .build();
    }

    // Login user
    public AuthResponse login(LoginRequest request) {
        log.info("Logging in user: {}", request.getEmail());

        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate token
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(convertToUserDTO(user))
                .build();
    }

    // Get user profile
    public UserDTO getProfile(String token) {
        String email = extractEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDTO(user);
    }

    // Update user profile
    public UserDTO updateProfile(String token, UserUpdateRequest request) {
        String email = extractEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return convertToUserDTO(user);
    }

    // Helper: Extract email from JWT token
    private String extractEmailFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        return jwtUtil.getEmailFromToken(token);
    }

    // Helper: Convert User to UserDTO
    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .role(user.getRole().toString())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

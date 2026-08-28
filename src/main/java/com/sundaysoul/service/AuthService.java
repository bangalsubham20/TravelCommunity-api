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
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // Utility to generate 6-digit OTP
    public String generateOtp() {
        return String.valueOf(
            java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 1000000)
        );
    }

    // Register new user
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Determine role: ADMIN only for specific emails
        boolean isAdmin = request.getEmail().equalsIgnoreCase("sumitkumar950840@gmail.com") ||
                request.getEmail().equalsIgnoreCase("bangalsubham@gmail.com");

        User.Role role = isAdmin ? User.Role.ADMIN : User.Role.USER;

        log.info("Creating {} user: {}", role, request.getEmail());

        String otp = generateOtp();

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .bio("Adventure enthusiast! 🌍")
                .avatar("https://via.placeholder.com/150")
                .role(role) // Set role based on email
                .emailVerified(false)
                .verificationOtp(otp)
                .otpExpiry(LocalDateTime.now().plusMinutes(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Send OTP verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), otp);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
        }

        return AuthResponse.builder()
                .message("Registration successful. Please check your email for verification OTP.")
                .user(convertToUserDTO(user))
                .build();
    }

    // Verify email
    public void verifyEmail(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        if (user.getOtpExpiry() == null ||
            user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        if (!user.getVerificationOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        user.setEmailVerified(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
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

        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before signing in");
        }

        // AUTO-PROMOTE ADMIN LOGIC
        // If email is in whitelist but role is USER, promote to ADMIN
        boolean isWhitelistedAdmin = request.getEmail().equalsIgnoreCase("sumitkumar950840@gmail.com") ||
                request.getEmail().equalsIgnoreCase("bangalsubham@gmail.com");

        if (isWhitelistedAdmin && user.getRole() != User.Role.ADMIN) {
            log.info("Auto-promoting user {} to ADMIN role", user.getEmail());
            user.setRole(User.Role.ADMIN);
            userRepository.save(user);
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

    // Get all users (Admin only)
    public java.util.List<UserDTO> getAllUsers(String token) {
        String email = extractEmailFromToken(token);
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Admin access required");
        }

        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .toList();
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
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

}

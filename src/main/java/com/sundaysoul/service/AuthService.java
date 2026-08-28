package com.sundaysoul.service;

import com.sundaysoul.config.JwtUtil;
import com.sundaysoul.dto.*;
import com.sundaysoul.model.PendingUser;
import com.sundaysoul.model.User;
import com.sundaysoul.repository.PendingUserRepository;
import com.sundaysoul.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingUserRepository pendingUserRepository;

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

    // Step 1: Initiate registration - save to PendingUser & send OTP email
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        // Check if user already exists in main UserRepository
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        String otp = generateOtp();

        // Send OTP verification email FIRST before saving pending registration
        try {
            emailService.sendVerificationEmail(request.getEmail(), otp);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send verification email. Registration aborted: " + e.getMessage());
        }

        // Save or update unverified user details in pending_users table (NOT users table)
        PendingUser pendingUser = pendingUserRepository.findByEmail(request.getEmail())
                .orElse(new PendingUser());

        pendingUser.setEmail(request.getEmail());
        pendingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        pendingUser.setFullName(request.getFullName());
        pendingUser.setPhone(request.getPhone());
        pendingUser.setOtp(otp);
        pendingUser.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        pendingUser.setCreatedAt(LocalDateTime.now());

        pendingUserRepository.save(pendingUser);

        return AuthResponse.builder()
                .message("Verification OTP sent to your email. Please verify OTP to complete registration.")
                .user(UserDTO.builder()
                        .name(request.getFullName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .emailVerified(false)
                        .build())
                .build();
    }

    // Step 2: Verify OTP - if successful, create User in database & delete PendingUser
    @Transactional
    public AuthResponse verifyEmail(String email, String otp) {
        // Check if already registered
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already verified and registered. Please log in.");
        }

        // Find pending registration record
        PendingUser pendingUser = pendingUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No pending registration found for this email. Please register first."));

        // Check OTP expiry
        if (pendingUser.getOtpExpiry() == null || pendingUser.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please register again to receive a new OTP.");
        }

        // Check OTP match
        if (!pendingUser.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP. Please check your verification code.");
        }

        // Determine role: ADMIN only for specific emails
        boolean isAdmin = email.equalsIgnoreCase("sumitkumar950840@gmail.com") ||
                email.equalsIgnoreCase("bangalsubham@gmail.com");

        User.Role role = isAdmin ? User.Role.ADMIN : User.Role.USER;

        // OTP is valid! NOW create and save the User in the main database
        User user = User.builder()
                .email(pendingUser.getEmail())
                .password(pendingUser.getPassword())
                .fullName(pendingUser.getFullName())
                .phone(pendingUser.getPhone())
                .bio("Adventure enthusiast! 🌍")
                .avatar("https://via.placeholder.com/150")
                .role(role)
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Remove pending record
        pendingUserRepository.delete(pendingUser);

        // Generate token for instant login upon verification
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .message("Email verified successfully! You are now registered.")
                .user(convertToUserDTO(user))
                .build();
    }

    // Login user
    public AuthResponse login(LoginRequest request) {
        log.info("Logging in user: {}", request.getEmail());

        // Check if pending verification exists
        if (pendingUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Please verify your email before signing in");
        }

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

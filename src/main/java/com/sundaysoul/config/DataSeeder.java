package com.sundaysoul.config;

import com.sundaysoul.model.User;
import com.sundaysoul.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                seedAdmin("bangalsubham@gmail.com", "Admin@Subham", "Subham Bangal");
                seedAdmin("sumitkumar950840@gmail.com", "Admin@Sumit", "Sumit Kumar");
        }

        private void seedAdmin(String email, String password, String fullName) {
                Optional<User> userOptional = userRepository.findByEmail(email);

                if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        // Force update password and role even if user exists
                        user.setRole(User.Role.ADMIN);
                        user.setPassword(passwordEncoder.encode(password));
                        user.setFullName(fullName);
                        user.setUpdatedAt(LocalDateTime.now());
                        userRepository.save(user);
                        System.out.println("Wait... " + email + " is already an Admin! Updated credentials.");
                } else {
                        User admin = User.builder()
                                        .email(email)
                                        .password(passwordEncoder.encode(password))
                                        .fullName(fullName)
                                        .phone("0000000000") // Default placeholder
                                        .role(User.Role.ADMIN)
                                        .bio("System Administrator")
                                        .avatar("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+")
                                                        + "&background=0D9488&color=fff")
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .build();

                        userRepository.save(admin);
                        System.out.println("Admin Created: " + email);
                }
        }
}

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
        private com.sundaysoul.repository.OfferRepository offerRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                String subhamPassword = System.getenv("ADMIN_SUBHAM_PASSWORD");
                String sumitPassword = System.getenv("ADMIN_SUMIT_PASSWORD");
                
                if (subhamPassword != null && !subhamPassword.isEmpty()) {
                        seedAdmin("bangalsubham@gmail.com", subhamPassword, "Subham Bangal");
                } else {
                        System.out.println("Skipping admin seed for bangalsubham@gmail.com. ADMIN_SUBHAM_PASSWORD not set.");
                }

                if (sumitPassword != null && !sumitPassword.isEmpty()) {
                        seedAdmin("sumitkumar950840@gmail.com", sumitPassword, "Sumit Kumar");
                } else {
                        System.out.println("Skipping admin seed for sumitkumar950840@gmail.com. ADMIN_SUMIT_PASSWORD not set.");
                }
                
                seedOffers();
        }

        private void seedOffers() {
                if (offerRepository.count() == 0) {
                        com.sundaysoul.model.Offer offer1 = com.sundaysoul.model.Offer.builder()
                                        .code("SUMMER10")
                                        .description("10% off on Summer Trips")
                                        .discount(10.0)
                                        .type(com.sundaysoul.model.Offer.OfferType.PERCENTAGE)
                                        .minAmount(5000.0)
                                        .active(true)
                                        .usageLimit(100)
                                        .usedCount(0)
                                        .validUntil(java.time.LocalDate.now().plusMonths(3))
                                        .build();

                        com.sundaysoul.model.Offer offer2 = com.sundaysoul.model.Offer.builder()
                                        .code("WELCOME500")
                                        .description("Flat ₹500 off on first booking")
                                        .discount(500.0)
                                        .type(com.sundaysoul.model.Offer.OfferType.FIXED)
                                        .minAmount(2000.0)
                                        .active(true)
                                        .usageLimit(50)
                                        .usedCount(0)
                                        .validUntil(java.time.LocalDate.now().plusMonths(6))
                                        .build();

                        offerRepository.saveAll(java.util.List.of(offer1, offer2));
                        System.out.println("Seeded sample offers.");
                }
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

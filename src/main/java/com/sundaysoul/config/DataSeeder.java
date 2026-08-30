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
        private com.sundaysoul.repository.TripRepository tripRepository;

        @Autowired
        private com.sundaysoul.repository.FAQRepository faqRepository;

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
                seedTrips();
                seedFAQs();
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

        private void seedTrips() {
                if (tripRepository.count() == 0) {
                        com.sundaysoul.model.Trip trip1 = com.sundaysoul.model.Trip.builder()
                                        .name("Manali Trek Expedition")
                                        .destination("Manali, Himachal Pradesh")
                                        .description("A perfect guide to your snow peak adventures through pine forests and alpine meadows.")
                                        .price(14500.0)
                                        .duration(7)
                                        .startDate(LocalDateTime.now().plusDays(10))
                                        .endDate(LocalDateTime.now().plusDays(17))
                                        .groupSize(15)
                                        .difficulty("Moderate")
                                        .season("Winter")
                                        .altitude("12,500 ft")
                                        .availableSeats(10)
                                        .image("https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Snow trail walking, Starlit camping, Solang Valley View")
                                        .rating(5.0)
                                        .reviews(70)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip2 = com.sundaysoul.model.Trip.builder()
                                        .name("Sikkim Kanchenjunga Trek")
                                        .destination("Sikkim, India")
                                        .description("Get up close to Mt. Kanchenjunga through rhododendron forests and sacred alpine lakes.")
                                        .price(18200.0)
                                        .duration(7)
                                        .startDate(LocalDateTime.now().plusDays(15))
                                        .endDate(LocalDateTime.now().plusDays(22))
                                        .groupSize(12)
                                        .difficulty("Challenging")
                                        .season("Summit")
                                        .altitude("15,100 ft")
                                        .availableSeats(8)
                                        .image("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Kanchenjunga View, Samiti Lake, Rhododendron Forest")
                                        .rating(4.9)
                                        .reviews(85)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip3 = com.sundaysoul.model.Trip.builder()
                                        .name("Snow Peak Manali Expedition")
                                        .destination("Manali, Himachal Pradesh")
                                        .description("High altitude snow peak trek with certified wilderness experts and mountain gear.")
                                        .price(16800.0)
                                        .duration(7)
                                        .startDate(LocalDateTime.now().plusDays(20))
                                        .endDate(LocalDateTime.now().plusDays(27))
                                        .groupSize(15)
                                        .difficulty("Moderate")
                                        .season("Winter")
                                        .altitude("13,800 ft")
                                        .availableSeats(12)
                                        .image("https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=800&auto=format&fit=crop")
                                        .highlights("High altitude snow trail, Panoramic Himalayan view, Camping")
                                        .rating(5.0)
                                        .reviews(92)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip4 = com.sundaysoul.model.Trip.builder()
                                        .name("Kedarkantha Summit Trek")
                                        .destination("Uttarakhand, India")
                                        .description("Walk through pine forests and snow trails to reach a breathtaking 360-degree Himalayan summit.")
                                        .price(12900.0)
                                        .duration(6)
                                        .startDate(LocalDateTime.now().plusDays(5))
                                        .endDate(LocalDateTime.now().plusDays(11))
                                        .groupSize(20)
                                        .difficulty("Beginner")
                                        .season("Summit")
                                        .altitude("12,500 ft")
                                        .availableSeats(15)
                                        .image("https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Snow trail walking, Starlit camping, Summit Sunrise")
                                        .rating(4.9)
                                        .reviews(110)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip5 = com.sundaysoul.model.Trip.builder()
                                        .name("Spiti Valley Cold Desert Trek")
                                        .destination("Himachal Pradesh, India")
                                        .description("Experience ancient monasteries, high altitude lakes, and dramatic canyon landscapes.")
                                        .price(22000.0)
                                        .duration(8)
                                        .startDate(LocalDateTime.now().plusDays(12))
                                        .endDate(LocalDateTime.now().plusDays(20))
                                        .groupSize(10)
                                        .difficulty("Moderate")
                                        .season("Camping")
                                        .altitude("14,000 ft")
                                        .availableSeats(6)
                                        .image("https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Key Monastery, Chandratal Lake, High Passes")
                                        .rating(4.8)
                                        .reviews(64)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip6 = com.sundaysoul.model.Trip.builder()
                                        .name("Ladakh Motorcycle Expedition")
                                        .destination("Leh Ladakh, India")
                                        .description("Ride through the world's highest motorable passes including Khardung La and Chang La.")
                                        .price(28500.0)
                                        .duration(9)
                                        .startDate(LocalDateTime.now().plusDays(18))
                                        .endDate(LocalDateTime.now().plusDays(27))
                                        .groupSize(12)
                                        .difficulty("Challenging")
                                        .season("Biking")
                                        .altitude("18,380 ft")
                                        .availableSeats(8)
                                        .image("https://images.unsplash.com/photo-1558981806-ec527fa84c39?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Khardung La, Pangong Tso Lake, Nubra Valley Quad Biking")
                                        .rating(4.9)
                                        .reviews(142)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip7 = com.sundaysoul.model.Trip.builder()
                                        .name("Meghalaya Caving & Waterfalls")
                                        .destination("Shillong, Meghalaya")
                                        .description("Explore living root bridges, crystal clear rivers, and deepest natural caves in East Asia.")
                                        .price(16500.0)
                                        .duration(6)
                                        .startDate(LocalDateTime.now().plusDays(8))
                                        .endDate(LocalDateTime.now().plusDays(14))
                                        .groupSize(14)
                                        .difficulty("Moderate")
                                        .season("Backpacking")
                                        .altitude("4,900 ft")
                                        .availableSeats(10)
                                        .image("https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Nohkalikai Falls, Cherrapunji, Living Root Bridge Cliff Jump")
                                        .rating(4.9)
                                        .reviews(78)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip8 = com.sundaysoul.model.Trip.builder()
                                        .name("Bali Tropical All-Girls Getaway")
                                        .destination("Bali, Indonesia")
                                        .description("An exclusive all-girls tropical escape across Ubud rice terraces, Nusa Penida beaches, and sunset clubs.")
                                        .price(45000.0)
                                        .duration(7)
                                        .startDate(LocalDateTime.now().plusDays(25))
                                        .endDate(LocalDateTime.now().plusDays(32))
                                        .groupSize(10)
                                        .difficulty("Easy")
                                        .season("All Girls")
                                        .altitude("1,500 ft")
                                        .availableSeats(6)
                                        .image("https://images.unsplash.com/photo-1537996194471-e657df975ab4?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Nusa Penida Kelingking Beach, Ubud Jungle Swing, Tanah Lot Sunset")
                                        .rating(5.0)
                                        .reviews(98)
                                        .active(true)
                                        .build();

                        com.sundaysoul.model.Trip trip9 = com.sundaysoul.model.Trip.builder()
                                        .name("Kasol & Kheerganga Weekend Escape")
                                        .destination("Kasol, Himachal Pradesh")
                                        .description("Unwind in Parvati Valley with hot sulfur springs and riverside acoustic music sessions.")
                                        .price(6500.0)
                                        .duration(3)
                                        .startDate(LocalDateTime.now().plusDays(3))
                                        .endDate(LocalDateTime.now().plusDays(6))
                                        .groupSize(18)
                                        .difficulty("Easy to Moderate")
                                        .season("Weekend")
                                        .altitude("9,700 ft")
                                        .availableSeats(12)
                                        .image("https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=800&auto=format&fit=crop")
                                        .highlights("Kheerganga Hot Springs, Cafe Hopping in Kasol, Manikaran Sahib")
                                        .rating(4.8)
                                        .reviews(156)
                                        .active(true)
                                        .build();

                        tripRepository.saveAll(java.util.List.of(trip1, trip2, trip3, trip4, trip5, trip6, trip7, trip8, trip9));
                        System.out.println("Seeded comprehensive database trips across all categories.");
                }
        }

        private void seedFAQs() {
                if (faqRepository.count() == 0) {
                        com.sundaysoul.model.FAQ faq1 = com.sundaysoul.model.FAQ.builder()
                                        .question("How do I book a trip on SundaySoul?")
                                        .answer("You can browse trips, select your preferred travel dates, click 'Book Now', and complete the instant verification checkout.")
                                        .category("Booking")
                                        .order(1)
                                        .build();

                        com.sundaysoul.model.FAQ faq2 = com.sundaysoul.model.FAQ.builder()
                                        .question("What safety measures are taken during high-altitude treks?")
                                        .answer("All our treks are led by Wilderness First Responder certified captains with oxygen cylinders, pulse oximeters, and first aid kits.")
                                        .category("Safety")
                                        .order(2)
                                        .build();

                        com.sundaysoul.model.FAQ faq3 = com.sundaysoul.model.FAQ.builder()
                                        .question("What is the cancellation policy?")
                                        .answer("Cancellations made 15 days before the trip start date receive a 100% full refund or credit voucher valid for 1 year.")
                                        .category("Cancellation")
                                        .order(3)
                                        .build();

                        com.sundaysoul.model.FAQ faq4 = com.sundaysoul.model.FAQ.builder()
                                        .question("Are all-girls trips safe for solo female travelers?")
                                        .answer("Yes! Our All-Girls trips feature verified female tour leads, vetted accommodations, and 24/7 dedicated support.")
                                        .category("Safety")
                                        .order(4)
                                        .build();

                        faqRepository.saveAll(java.util.List.of(faq1, faq2, faq3, faq4));
                        System.out.println("Seeded FAQs into database.");
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
                        user.setEmailVerified(true);
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
                                        .emailVerified(true)
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

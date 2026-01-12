package com.travelcommunity.service;

import com.travelcommunity.config.JwtUtil;
import com.travelcommunity.dto.BookingRequest;
import com.travelcommunity.dto.TravelerDTO;
import com.travelcommunity.model.*;
import com.travelcommunity.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Create booking
    public Booking createBooking(BookingRequest request, String token) {
        log.info("Creating booking for trip ID: {}", request.getTripId());

        // Get user from token
        User user = getUserFromToken(token);

        // Get trip
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Check available seats
        if (trip.getAvailableSeats() < request.getNumTravelers()) {
            throw new RuntimeException("Not enough available seats");
        }

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .trip(trip)
                .numTravelers(request.getNumTravelers())
                .totalPrice(request.getTotalPrice())
                .paymentMethod(request.getPaymentMethod())
                .build();

        // Add travelers
        List<Traveler> travelers = request.getTravelers().stream()
                .map(travelerReq -> Traveler.builder()
                        .booking(booking)
                        .fullName(travelerReq.getFullName())
                        .age(travelerReq.getAge())
                        .gender(travelerReq.getGender())
                        .phone(travelerReq.getPhone())
                        .email(travelerReq.getEmail())
                        .emergencyContact(travelerReq.getEmergencyContact())
                        .dietaryRestrictions(travelerReq.getDietaryRestrictions())
                        .build())
                .toList();

        booking.setTravelers(travelers);

        // Update available seats
        trip.setAvailableSeats(trip.getAvailableSeats() - request.getNumTravelers());
        tripRepository.save(trip);

        return bookingRepository.save(booking);
    }

    // Get user bookings
    public List<Booking> getUserBookings(String token) {
        User user = getUserFromToken(token);
        log.info("Fetching bookings for user: {}", user.getEmail());
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    // Get booking by ID
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // Cancel booking
    public Booking cancelBooking(Long id, String token) {
        User user = getUserFromToken(token);
        log.info("Cancelling booking ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Can only cancel pending bookings");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        // Restore available seats
        Trip trip = booking.getTrip();
        trip.setAvailableSeats(trip.getAvailableSeats() + booking.getNumTravelers());
        tripRepository.save(trip);

        return bookingRepository.save(booking);
    }

    // Get all bookings (Admin only)
    public List<Booking> getAllBookings(String token) {
        User user = verifyAdmin(token);
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    // Update booking status (Admin only)
    public Booking updateBookingStatus(Long id, String status, String token) {
        User user = verifyAdmin(token);
        log.info("Updating booking ID: {} status to: {}", id, status);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Booking.BookingStatus.valueOf(status.toUpperCase()));
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    // Helper: Get user from token
    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        String email = jwtUtil.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper: Verify admin
    private User verifyAdmin(String token) {
        User user = getUserFromToken(token);
        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Admin access required");
        }
        return user;
    }
}

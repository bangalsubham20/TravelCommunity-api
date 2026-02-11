package com.sundaysoul.service;

import com.sundaysoul.config.JwtUtil;
import com.sundaysoul.model.Trip;
import com.sundaysoul.model.User;
import com.sundaysoul.repository.TripRepository;
import com.sundaysoul.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TripService {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // Get all trips with filters
    public List<Trip> getAllTrips(Long minPrice, Long maxPrice, String difficulty,
            String destination, String season, String search, String sortBy) {
        log.info("Fetching trips with filters");

        List<Trip> trips = tripRepository.findByActive(true);

        // Apply filters
        if (search != null && !search.isEmpty()) {
            trips = trips.stream()
                    .filter(t -> t.getName().toLowerCase().contains(search.toLowerCase()) ||
                            t.getDestination().toLowerCase().contains(search.toLowerCase()) ||
                            t.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (minPrice != null && maxPrice != null) {
            trips = trips.stream()
                    .filter(t -> t.getPrice() >= minPrice && t.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (difficulty != null && !difficulty.isEmpty()) {
            trips = trips.stream()
                    .filter(t -> t.getDifficulty().equalsIgnoreCase(difficulty))
                    .collect(Collectors.toList());
        }

        if (destination != null && !destination.isEmpty()) {
            trips = trips.stream()
                    .filter(t -> t.getDestination().equalsIgnoreCase(destination))
                    .collect(Collectors.toList());
        }

        if (season != null && !season.isEmpty()) {
            trips = trips.stream()
                    .filter(t -> t.getSeason().equalsIgnoreCase(season))
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sortBy != null) {
            switch (sortBy) {
                case "priceLowToHigh":
                    trips.sort(Comparator.comparingDouble(Trip::getPrice));
                    break;
                case "priceHighToLow":
                    trips.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                    break;
                case "ratingHighToLow":
                    trips.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                    break;
                case "durationShortToLong":
                    trips.sort(Comparator.comparingInt(Trip::getDuration));
                    break;
            }
        }

        return trips;
    }

    // Get trip by ID
    public Trip getTripById(Long id) {
        log.info("Fetching trip with ID: {}", id);
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    // Search trips
    public List<Trip> searchTrips(String searchTerm) {
        log.info("Searching trips with term: {}", searchTerm);
        return tripRepository.searchTrips(searchTerm);
    }

    // Create trip (Admin only)
    public Trip createTrip(Trip trip, String token) {
        log.info("Creating new trip: {}", trip.getName());

        // Verify admin
        User user = verifyAdmin(token);

        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());
        trip.setActive(true);

        return tripRepository.save(trip);
    }

    // Update trip (Admin only)
    public Trip updateTrip(Long id, Trip tripDetails, String token) {
        log.info("Updating trip with ID: {}", id);

        // Verify admin
        User user = verifyAdmin(token);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        trip.setName(tripDetails.getName());
        trip.setDestination(tripDetails.getDestination());
        trip.setDescription(tripDetails.getDescription());
        trip.setPrice(tripDetails.getPrice());
        trip.setDuration(tripDetails.getDuration());
        trip.setDifficulty(tripDetails.getDifficulty());
        trip.setAvailableSeats(tripDetails.getAvailableSeats());
        trip.setUpdatedAt(LocalDateTime.now());

        return tripRepository.save(trip);
    }

    // Delete trip (Admin only)
    public void deleteTrip(Long id, String token) {
        log.info("Deleting trip with ID: {}", id);

        // Verify admin
        User user = verifyAdmin(token);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        trip.setActive(false);
        tripRepository.save(trip);
    }

    // Get trip reviews
    public List<?> getTripReviews(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return new ArrayList<>(); // Will implement with Review service
    }

    // Helper: Verify admin user
    private User verifyAdmin(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Admin access required");
        }
        return user;
    }
}

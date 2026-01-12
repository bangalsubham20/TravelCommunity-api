package com.travelcommunity.service;

import com.travelcommunity.config.JwtUtil;
import com.travelcommunity.model.Review;
import com.travelcommunity.model.Trip;
import com.travelcommunity.model.User;
import com.travelcommunity.repository.ReviewRepository;
import com.travelcommunity.repository.TripRepository;
import com.travelcommunity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Create review
    public Review createReview(Long tripId, Review review, String token) {
        log.info("Creating review for trip ID: {}", tripId);

        User user = getUserFromToken(token);
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Check if user already reviewed
        if (reviewRepository.findByTripAndUser(trip, user).isPresent()) {
            throw new RuntimeException("You already reviewed this trip");
        }

        review.setTrip(trip);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // Update trip rating
        updateTripRating(trip);

        return savedReview;
    }

    // Get trip reviews
    public List<Review> getTripReviews(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return reviewRepository.findByTrip(trip);
    }

    // Get user reviews
    public List<Review> getUserReviews(String token) {
        User user = getUserFromToken(token);
        log.info("Fetching reviews for user: {}", user.getEmail());
        return reviewRepository.findByUser(user);
    }

    // Update review
    public Review updateReview(Long id, Review reviewDetails, String token) {
        User user = getUserFromToken(token);
        log.info("Updating review ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        review.setRating(reviewDetails.getRating());
        review.setTitle(reviewDetails.getTitle());
        review.setText(reviewDetails.getText());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);

        // Update trip rating
        updateTripRating(review.getTrip());

        return updatedReview;
    }

    // Delete review
    public void deleteReview(Long id, String token) {
        User user = getUserFromToken(token);
        log.info("Deleting review ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        Trip trip = review.getTrip();
        reviewRepository.deleteById(id);

        // Update trip rating
        updateTripRating(trip);
    }

    // Helper: Update trip rating
    private void updateTripRating(Trip trip) {
        List<Review> reviews = reviewRepository.findByTrip(trip);
        if (reviews.isEmpty()) {
            trip.setRating(0.0);
            trip.setReviews(0);
        } else {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            trip.setRating(Math.round(avgRating * 10.0) / 10.0);
            trip.setReviews(reviews.size());
        }
        tripRepository.save(trip);
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
}

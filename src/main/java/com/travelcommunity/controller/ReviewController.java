package com.travelcommunity.controller;

import com.travelcommunity.dto.*;
import com.travelcommunity.model.Review;
import com.travelcommunity.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Review review = new Review();
            review.setRating(request.getRating());
            review.setTitle(request.getTitle());
            review.setText(request.getText());
            return ResponseEntity.ok(reviewService.createReview(request.getTripId(), review, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Review>> getTripReviews(@PathVariable Long tripId) {
        return ResponseEntity.ok(reviewService.getTripReviews(tripId));
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<Review>> getUserReviews(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(reviewService.getUserReviews(token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Review review = new Review();
            review.setRating(request.getRating());
            review.setTitle(request.getTitle());
            review.setText(request.getText());
            return ResponseEntity.ok(reviewService.updateReview(id, review, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            reviewService.deleteReview(id, token);
            return ResponseEntity.ok(new MessageResponse("Review deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

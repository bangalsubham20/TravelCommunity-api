package com.travelcommunity.controller;

import com.travelcommunity.dto.*;
import com.travelcommunity.model.Trip;
import com.travelcommunity.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    @Autowired
    private TripService tripService;

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips(
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity
                .ok(tripService.getAllTrips(minPrice, maxPrice, difficulty, destination, season, search, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Trip>> searchTrips(@RequestParam String q) {
        return ResponseEntity.ok(tripService.searchTrips(q));
    }

    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody Trip trip, @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(tripService.createTrip(trip, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrip(@PathVariable Long id, @RequestBody Trip trip,
            @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(tripService.updateTrip(id, trip, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            tripService.deleteTrip(id, token);
            return ResponseEntity.ok(new MessageResponse("Trip deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getTripReviews(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripReviews(id));
    }
}

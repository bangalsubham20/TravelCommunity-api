package com.sundaysoul.controller;

import com.sundaysoul.model.Offer;
import com.sundaysoul.service.OfferService;
import com.sundaysoul.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @PostMapping
    public ResponseEntity<?> createOffer(@RequestHeader("Authorization") String token, @RequestBody Offer offer) {
        try {
            return ResponseEntity.ok(offerService.createOffer(token, offer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOffer(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            offerService.deleteOffer(token, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActive(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(offerService.toggleActive(token, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validateOffer(@PathVariable String code) {
        try {
            return ResponseEntity.ok(offerService.validateOffer(code));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

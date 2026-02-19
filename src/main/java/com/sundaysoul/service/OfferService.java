package com.sundaysoul.service;

import com.sundaysoul.model.Offer;
import com.sundaysoul.model.User;
import com.sundaysoul.repository.OfferRepository;
import com.sundaysoul.repository.UserRepository;
import com.sundaysoul.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Create a new offer (Admin only)
    public Offer createOffer(String token, Offer offer) {
        verifyAdmin(token);
        if (offerRepository.findByCode(offer.getCode()).isPresent()) {
            throw new RuntimeException("Offer code already exists");
        }
        offer.setUsedCount(0);
        return offerRepository.save(offer);
    }

    // Get all offers (Admin/Public use case depending on needs, exposed to Admin
    // here)
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    // Delete offer (Admin only)
    public void deleteOffer(String token, Long id) {
        verifyAdmin(token);
        offerRepository.deleteById(id);
    }

    // Toggle offer active status (Admin only)
    public Offer toggleActive(String token, Long id) {
        verifyAdmin(token);
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        offer.setActive(!offer.isActive());
        return offerRepository.save(offer);
    }

    // Validate offer code (Public)
    public Offer validateOffer(String code) {
        Optional<Offer> offerOpt = offerRepository.findByCode(code);
        if (offerOpt.isEmpty()) {
            throw new RuntimeException("Invalid offer code");
        }
        Offer offer = offerOpt.get();
        if (!offer.isActive()) {
            throw new RuntimeException("Offer is inactive");
        }
        if (offer.getValidUntil() != null && offer.getValidUntil().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("Offer has expired");
        }
        if (offer.getUsageLimit() != null && offer.getUsedCount() >= offer.getUsageLimit()) {
            throw new RuntimeException("Offer usage limit exceeded");
        }
        return offer;
    }

    private void verifyAdmin(String token) {
        String email = jwtUtil.getEmailFromToken(token.substring(7));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Admin access required");
        }
    }
}

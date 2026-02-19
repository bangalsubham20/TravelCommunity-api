package com.sundaysoul.controller;

import com.sundaysoul.model.ContactMessage;
import com.sundaysoul.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContactController {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @PostMapping("/contact")
    public ResponseEntity<?> submitContact(@RequestBody ContactMessage message) {
        try {
            message.setStatus(ContactMessage.Status.NEW);
            ContactMessage saved = contactMessageRepository.save(message);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit message: " + e.getMessage());
        }
    }

    @GetMapping("/admin/contact")
    public ResponseEntity<?> getAllMessages(@RequestHeader("Authorization") String token) {
        // In a real app, validate token & admin role here or via SecurityConfig
        List<ContactMessage> messages = contactMessageRepository.findAll();
        // Sort by ID desc (newest first)
        messages.sort((a, b) -> b.getId().compareTo(a.getId()));
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/admin/contact/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody ContactMessage.Status status) {
        return contactMessageRepository.findById(id)
                .map(msg -> {
                    msg.setStatus(status);
                    return ResponseEntity.ok(contactMessageRepository.save(msg));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.sundaysoul.controller;

import com.sundaysoul.model.FAQ;
import com.sundaysoul.model.StaticPage;
import com.sundaysoul.repository.FAQRepository;
import com.sundaysoul.repository.StaticPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    private FAQRepository faqRepository;

    @Autowired
    private StaticPageRepository staticPageRepository;

    // --- FAQ Endpoints ---

    @GetMapping("/faq")
    public ResponseEntity<List<FAQ>> getFAQs() {
        return ResponseEntity.ok(faqRepository.findAllByOrderByOrderAsc());
    }

    @PostMapping("/admin/faq")
    public ResponseEntity<?> createFAQ(@RequestBody FAQ faq) {
        return ResponseEntity.ok(faqRepository.save(faq));
    }

    @PutMapping("/admin/faq/{id}")
    public ResponseEntity<?> updateFAQ(@PathVariable Long id, @RequestBody FAQ faqDetails) {
        return faqRepository.findById(id)
                .map(faq -> {
                    faq.setQuestion(faqDetails.getQuestion());
                    faq.setAnswer(faqDetails.getAnswer());
                    faq.setCategory(faqDetails.getCategory());
                    faq.setOrder(faqDetails.getOrder());
                    return ResponseEntity.ok(faqRepository.save(faq));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/faq/{id}")
    public ResponseEntity<?> deleteFAQ(@PathVariable Long id) {
        if (faqRepository.existsById(id)) {
            faqRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Static Page Endpoints ---

    @GetMapping("/pages/{slug}")
    public ResponseEntity<StaticPage> getPage(@PathVariable String slug) {
        return staticPageRepository.findById(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    // Return default empty content if not found, to avoid 404s on frontend
                    return ResponseEntity.ok(StaticPage.builder()
                            .slug(slug)
                            .title(slug.substring(0, 1).toUpperCase() + slug.substring(1))
                            .content("<p>Content coming soon...</p>")
                            .lastUpdated(LocalDateTime.now())
                            .build());
                });
    }

    @PutMapping("/admin/pages/{slug}")
    public ResponseEntity<?> updatePage(@PathVariable String slug, @RequestBody StaticPage pageDetails) {
        StaticPage page = staticPageRepository.findById(slug)
                .orElse(StaticPage.builder().slug(slug).build());

        page.setTitle(pageDetails.getTitle());
        page.setContent(pageDetails.getContent());
        // lastUpdated is handled by @PreUpdate/@PrePersist

        return ResponseEntity.ok(staticPageRepository.save(page));
    }
}

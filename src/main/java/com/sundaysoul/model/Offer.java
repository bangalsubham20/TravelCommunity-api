package com.sundaysoul.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "offers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    @Column(nullable = false)
    private Double discount;

    @Enumerated(EnumType.STRING)
    private OfferType type; // PERCENTAGE or FIXED

    private Double minAmount;

    private LocalDate validUntil;

    private Integer usageLimit;

    private Integer usedCount;

    private boolean active;

    public enum OfferType {
        PERCENTAGE, FIXED
    }
}

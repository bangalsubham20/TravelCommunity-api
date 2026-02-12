package com.sundaysoul.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String destination;

    @Column(length = 1000) // REDUCED from 2000
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer groupSize;

    @Column(nullable = false)
    private String difficulty;

    @Column
    private String season;

    @Column
    private String altitude;

    @Column
    private String bestSeason;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column
    private String image;

    @Column(length = 2000) // REDUCED from 5000
    private String highlights;

    @Column(length = 2000) // REDUCED from 5000
    private String itinerary;

    @Column(length = 1500) // REDUCED from 3000
    private String inclusions;

    @Column(length = 1500) // REDUCED from 3000
    private String exclusions;

    @Column(nullable = false)
    @Builder.Default
    private Double rating = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reviews = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Review> tripReviews;
}

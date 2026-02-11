package com.sundaysoul.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "travelers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Traveler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column
    private String emergencyContact;

    @Column
    private String dietaryRestrictions;
}

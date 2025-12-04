package com.wravelcommunity.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long tripId;
    private Integer numTravelers;
    private Double totalPrice;
    private String paymentMethod;
    private List<TravelerDTO> travelers;
}

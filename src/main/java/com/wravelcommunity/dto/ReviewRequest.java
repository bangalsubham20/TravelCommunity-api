package com.wravelcommunity.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Long tripId;
    private Integer rating;
    private String title;
    private String text;
}

package com.sundaysoul.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyEmailRequest {
    private String email;
    private String otp;
}

package com.wravelcommunity.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String bio;
    private String avatar;
    private String role;
    private LocalDateTime createdAt;
}

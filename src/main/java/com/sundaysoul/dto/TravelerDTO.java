package com.sundaysoul.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelerDTO {
    private String fullName;
    private Integer age;
    private String gender;
    private String phone;
    private String email;
    private String emergencyContact;
    private String dietaryRestrictions;
}

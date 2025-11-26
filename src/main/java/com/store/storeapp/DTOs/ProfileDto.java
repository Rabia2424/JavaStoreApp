package com.store.storeapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String username;
    private String email;
}

package org.example.moodshare.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String bio;
    private String profilePicture;
} 
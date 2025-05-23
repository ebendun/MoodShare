package org.example.moodshare.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String bio;//个人简介
    private String profilePicture;
} 
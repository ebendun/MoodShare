package org.example.moodshare.dto;


import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String captchaCode;
    private String sessionId;
}

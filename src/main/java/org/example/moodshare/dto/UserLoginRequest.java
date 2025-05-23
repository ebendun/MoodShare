package org.example.moodshare.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}

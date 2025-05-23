package org.example.moodshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
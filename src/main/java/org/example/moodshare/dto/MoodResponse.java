// 心情响应DTO
package org.example.moodshare.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class MoodResponse {
    private Long id;
    private String content;
    private String emoji;
    private Set<String> tags;
    private LocalDateTime createdAt;
    private String username;
    private int likeCount;
    private boolean likedByCurrentUser;
    private List<CommentResponse> comments;
}
// 评论请求和响应
package org.example.moodshare.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String username;
    private Long userId;
    private String userProfilePicture;
}
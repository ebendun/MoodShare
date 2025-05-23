// 心情响应DTO
package org.example.moodshare.dto;

import lombok.Data;
import org.example.moodshare.model.Mood;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
//服务器端返回给客户端的心情内容
@Data
public class MoodResponse {
    private Long id;
    private String content;
    private String emoji;
    private Set<String> tags;
    private LocalDateTime createdAt;
    private UserDto user;
    private List<CommentDto> comments;
    private int likeCount;
    private boolean liked;
    
    // 新增隐私设置
    private Mood.PrivacyLevel privacyLevel;
    
    // 新增位置信息
    private String location;
    private Double latitude;
    private Double longitude;
    
    // 新增心情类型
    private Mood.MoodType moodType;
    
    // 天气信息
    private String weather;
    
    @Data
    public static class UserDto {
        private Long id;
        private String username;
        private String profilePicture;
    }
    
    @Data
    public static class CommentDto {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private UserDto user;
    }
}
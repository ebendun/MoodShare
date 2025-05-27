// 评论请求和响应
package org.example.moodshare.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponse {//服务器端返回给客户端的评论内容
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String username;//评论者的用户名
    private Long userId;
    private String userProfilePicture;//评论者的头像
    private UserInfo user; // 添加用户信息对象
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String profilePicture;
    }
}
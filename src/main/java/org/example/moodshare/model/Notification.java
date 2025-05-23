package org.example.moodshare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;//通知的接收者

    private String content;//通知内容

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    private Long referenceId;

    @Column(name = "is_read", columnDefinition = "boolean default false")
    private boolean isRead = false;//通知默认未读

    private LocalDateTime createdAt = LocalDateTime.now();

    // 通知类型枚举
    public enum NotificationType {
        MOOD_LIKE,     // 心情点赞
        MOOD_COMMENT,  // 心情评论
        FRIEND_REQUEST, // 好友请求
        FRIEND_ACCEPT,  // 好友接受
        SYSTEM_MESSAGE  // 系统消息
    }
}
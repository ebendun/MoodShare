package org.example.moodshare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = {"user", "mood"})
@EqualsAndHashCode(exclude = {"user", "mood"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 评论内容
    private LocalDateTime createdAt = LocalDateTime.now();// 评论时间

    @ManyToOne
    private User user;

    @ManyToOne
    private Mood mood;
}

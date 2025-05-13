package org.example.moodshare.model;


import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.*;


@Entity
@Data
public class Mood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private String emoji;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "mood_likes",
            joinColumns = @JoinColumn(name = "mood_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedBy = new HashSet<>();

    // 隐私设置
    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacyLevel = PrivacyLevel.PUBLIC;

    // 位置信息
    private String location;
    private Double latitude;
    private Double longitude;
    
    // 心情类型
    @Enumerated(EnumType.STRING)
    private MoodType moodType = MoodType.NEUTRAL;
    
    // 天气信息
    private String weather;

    public int getLikeCount() {
        return likedBy.size();
    }
    
    // 隐私级别枚举
    public enum PrivacyLevel {
        PUBLIC,     // 公开，所有人可见
        FRIENDS,    // 好友可见
        PRIVATE     // 仅自己可见
    }
    
    // 心情类型枚举
    public enum MoodType {
        HAPPY,      // 开心
        SAD,        // 悲伤
        ANGRY,      // 生气
        EXCITED,    // 兴奋
        WORRIED,    // 担忧
        NEUTRAL     // 平静
    }
}

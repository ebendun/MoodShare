package org.example.moodshare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@ToString(exclude = {"friends", "friendRequests"})
@EqualsAndHashCode(exclude = {"friends", "friendRequests"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private String role = ROLE_USER; // 默认角色
    
    // 个人资料字段
    private String profilePicture;//头像
    private String bio;//个人简介
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLoginAt;//最后登录时间
    
    // 好友关系
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    // 好友请求
    @ManyToMany
    @JoinTable(
        name = "friend_requests",
        joinColumns = @JoinColumn(name = "receiver_id"),
        inverseJoinColumns = @JoinColumn(name = "sender_id")
    )
    private Set<User> friendRequests = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(this.role);
    }
}

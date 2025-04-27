package org.example.moodshare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
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
    private String profilePicture;
    private String bio;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLoginAt;
    
    // 好友关系
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();
    
    // 好友请求
    @ManyToMany
    @JoinTable(
        name = "friend_requests",
        joinColumns = @JoinColumn(name = "receiver_id"),
        inverseJoinColumns = @JoinColumn(name = "sender_id")
    )
    private Set<User> friendRequests = new HashSet<>();

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(this.role);
    }
}

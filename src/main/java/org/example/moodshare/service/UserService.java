package org.example.moodshare.service;

import org.example.moodshare.repository.UserRepository;
import org.example.moodshare.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String username, String email, String password) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already in use");
        }
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER"); // Default role
        return userRepository.save(user);
    }
    
    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
    }

    
    /**
     * 更新用户资料
     */
    public User updateProfile(User user, String bio, String profilePicture) {
        user.setBio(bio);
        user.setProfilePicture(profilePicture);
        return userRepository.save(user);
    }
    
    /**
     * 修改用户密码
     */
    public User changePassword(String username, String oldPassword, String newPassword) {
        System.out.println("开始修改密码 - 用户名: " + username);
        User user = getUserByUsername(username);
        System.out.println("找到用户: " + user.getUsername() + ", 当前密码哈希: " + user.getPassword());
        
        // 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            System.out.println("旧密码验证失败");
            throw new org.springframework.security.authentication.BadCredentialsException("旧密码不正确");
        }
        System.out.println("旧密码验证成功");
        
        // 更新密码
        String oldPasswordHash = user.getPassword();
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPassword(newPasswordHash);
        System.out.println("旧密码哈希: " + oldPasswordHash);
        System.out.println("新密码哈希: " + newPasswordHash);
        
        User savedUser = userRepository.save(user);
        System.out.println("密码修改完成，保存后的密码哈希: " + savedUser.getPassword());
        return savedUser;
    }
    
    /**
     * 根据用户名模糊搜索用户
     */
    public List<User> searchUsersByUsername(String username) {
        System.out.println("搜索用户名: " + username);
        List<User> users = userRepository.findByUsernameContaining(username);
        System.out.println("找到匹配用户数: " + users.size());
        if (!users.isEmpty()) {
            for (User user : users) {
                System.out.println("- ID: " + user.getId() + ", 用户名: " + user.getUsername());
            }
        }
        return users;
    }
    
    /**
     * 根据用户ID获取用户
     */
    public User getUserById(Long userId) {
        System.out.println("通过ID获取用户: " + userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("错误: 找不到用户, ID = " + userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
                });
    }
}

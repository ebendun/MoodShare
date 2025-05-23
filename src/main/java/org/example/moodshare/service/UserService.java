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
        User user = getUserByUsername(username);
        
        // 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException("旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    /**
     * 根据用户名模糊搜索用户
     */
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }
}

package org.example.moodshare.service;

import org.example.moodshare.repository.UserRepository;
import org.example.moodshare.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * 根据ID获取用户
     */
    public User getById(Long id) {
        return userRepository.findById(id)
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
}

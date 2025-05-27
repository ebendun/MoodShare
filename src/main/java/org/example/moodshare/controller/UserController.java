package org.example.moodshare.controller;

import org.example.moodshare.dto.UserRegistrationRequest;
import org.example.moodshare.dto.UserLoginRequest;
import org.example.moodshare.dto.UserProfileUpdateRequest;
import org.example.moodshare.model.User;
import org.example.moodshare.service.UserService;
import org.example.moodshare.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth") // Changed from "/api/auth" to match frontend requests
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        logger.info("收到注册请求: username={}, email={}", request.getUsername(), request.getEmail());
        try {
            User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
            logger.info("用户注册成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "注册成功",
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail()
                    )
            ));
        } catch (RuntimeException e) {
            logger.error("注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtil.generateToken(authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", authentication.getName()
            ));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", STR."登录失败：\{ex.getMessage()}"
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "username", authentication.getName()
            ));
        }
        return ResponseEntity.ok(Map.of(
                "authenticated", false
        ));
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("获取当前用户信息: {}", userDetails != null ? userDetails.getUsername() : "未登录");
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "未登录"
            ));
        }
        
        try {
            User user = userService.getUserByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "用户不存在"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("bio", user.getBio());
            response.put("profilePicture", user.getProfilePicture());
            response.put("createdAt", user.getCreatedAt());
            response.put("lastLoginAt", user.getLastLoginAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false, 
                    "message", "获取用户信息失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserProfileUpdateRequest request) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        user = userService.updateProfile(user, request.getBio(), request.getProfilePicture());
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("bio", user.getBio());
        response.put("profilePicture", user.getProfilePicture());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String username) {
        List<User> users = userService.searchUsersByUsername(username);
        
        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("profilePicture", user.getProfilePicture());
            return userMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取指定用户的公开信息
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        logger.debug("获取用户信息: userId={}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "用户不存在"
                ));
            }
            
            // 只返回公开信息，不包含邮箱等敏感信息
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("bio", user.getBio());
            response.put("profilePicture", user.getProfilePicture());
            response.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false, 
                    "message", "获取用户信息失败: " + e.getMessage()
            ));
        }
    }
}

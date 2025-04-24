package org.example.moodshare.controller;

import org.example.moodshare.dto.UserRegistrationRequest;
import org.example.moodshare.dto.UserLoginRequest;
import org.example.moodshare.model.User;
import org.example.moodshare.service.UserService;
import org.example.moodshare.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
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
                    "message", "登录失败：" + ex.getMessage()
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
}

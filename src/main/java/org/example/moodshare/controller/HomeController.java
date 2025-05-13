package org.example.moodshare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "欢迎使用MoodShare API");
        response.put("status", "运行中");
        response.put("version", "1.0.0");
        return response;
    }
    
    @GetMapping("/api")
    public Map<String, Object> api() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "MoodShare API");
        response.put("status", "可用");
        response.put("endpoints", new String[] {
            "/api/auth/login",
            "/api/auth/register", 
            "/api/moods",
            "/api/friends",
            "/api/notifications" 
        });
        return response;
    }
} 
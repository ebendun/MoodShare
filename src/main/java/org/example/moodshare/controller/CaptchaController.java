package org.example.moodshare.controller;

import org.example.moodshare.service.CaptchaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public ResponseEntity<?> getCaptcha() {
        try {
            // 生成唯一的会话ID
            String sessionId = UUID.randomUUID().toString();
            
            // 生成验证码
            Map<String, Object> captchaData = captchaService.generateCaptcha(sessionId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", captchaData
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "生成验证码失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 验证图形验证码（可选的独立验证接口）
     */
    @PostMapping("/captcha/verify")
    public ResponseEntity<?> verifyCaptcha(@RequestBody Map<String, String> request) {
        try {
            String sessionId = request.get("sessionId");
            String captchaCode = request.get("captchaCode");
            
            boolean isValid = captchaService.validateCaptcha(sessionId, captchaCode);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "valid", isValid,
                    "message", isValid ? "验证码正确" : "验证码错误或已过期"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "验证码校验失败: " + e.getMessage()
            ));
        }
    }
}

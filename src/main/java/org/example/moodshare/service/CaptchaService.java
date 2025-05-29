package org.example.moodshare.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    private final RedisTemplate<String, String> redisTemplate;

    public CaptchaService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成图形验证码
     * @param sessionId 会话ID
     * @return 包含验证码图片的Map
     */
    public Map<String, Object> generateCaptcha(String sessionId) {
        // 创建圆圈干扰的图形验证码，宽200，高80，验证码字符数4，干扰元素个数20
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 80, 4, 20);
        
        // 获取验证码的文字内容
        String captchaCode = captcha.getCode();
        
        // 将验证码存储到Redis中，设置5分钟过期
        String redisKey = "captcha:" + sessionId;
        redisTemplate.opsForValue().set(redisKey, captchaCode.toLowerCase(), 5, TimeUnit.MINUTES);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("captchaImage", captcha.getImageBase64Data()); // base64格式的图片
        result.put("sessionId", sessionId);
        
        return result;
    }

    /**
     * 验证图形验证码
     * @param sessionId 会话ID
     * @param userInput 用户输入的验证码
     * @return 验证是否成功
     */
    public boolean validateCaptcha(String sessionId, String userInput) {
        if (sessionId == null || userInput == null || userInput.trim().isEmpty()) {
            return false;
        }
        
        String redisKey = "captcha:" + sessionId;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            return false; // 验证码已过期或不存在
        }
        
        // 验证成功后删除验证码（一次性使用）
        boolean isValid = storedCode.equalsIgnoreCase(userInput.trim());
        if (isValid) {
            redisTemplate.delete(redisKey);
        }
        
        return isValid;
    }

    /**
     * 删除验证码
     * @param sessionId 会话ID
     */
    public void removeCaptcha(String sessionId) {
        if (sessionId != null) {
            String redisKey = "captcha:" + sessionId;
            redisTemplate.delete(redisKey);
        }
    }
}

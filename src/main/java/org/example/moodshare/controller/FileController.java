package org.example.moodshare.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    // 上传文件保存路径，默认为当前项目的 uploads 目录
    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;
    
    /**
     * 上传头像图片
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<?> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "未登录"
            ));
        }
        
        try {
            // 确保目录存在
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
              // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // 保存文件
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建文件URL
            String fileUrl = "/uploads/" + filename;
            
            logger.info("用户 {} 上传头像成功: {}", userDetails.getUsername(), fileUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "头像上传成功");
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException ex) {
            logger.error("上传头像失败: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "上传头像失败: " + ex.getMessage()
            ));
        }
    }
    
    /**
     * 上传心情图片
     */
    @PostMapping("/upload/mood")
    public ResponseEntity<?> uploadMoodImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "未登录"
            ));
        }
        
        try {
            // 确保目录存在
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // 保存文件
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建文件URL
            String fileUrl = "/uploads/" + filename;
            
            logger.info("用户 {} 上传心情图片成功: {}", userDetails.getUsername(), fileUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图片上传成功");
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException ex) {
            logger.error("上传心情图片失败: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "上传图片失败: " + ex.getMessage()
            ));
        }
    }
}

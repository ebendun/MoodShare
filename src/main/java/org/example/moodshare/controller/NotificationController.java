package org.example.moodshare.controller;

import org.example.moodshare.model.Notification;
import org.example.moodshare.model.User;
import org.example.moodshare.service.NotificationService;
import org.example.moodshare.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications") // Changed from "/api/notifications" to match frontend requests
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    /**
     * 获取当前的用户
     */
    private User getUserFromUserDetails(UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }
    /**
     * 获取所有通知
     */
    @GetMapping
    public ResponseEntity<?> getAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("获取用户[{}]的所有通知", userDetails.getUsername());
        User currentUser = getUserFromUserDetails(userDetails);
        List<Notification> notifications = notificationService.getUserNotifications(currentUser);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", notifications
        ));
    }
    
    /**
     * 获取未读通知
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("获取用户[{}]的未读通知", userDetails.getUsername());
        User currentUser = getUserFromUserDetails(userDetails);
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(currentUser);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", unreadNotifications
        ));
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("获取用户[{}]的未读通知数量", userDetails.getUsername());
        User currentUser = getUserFromUserDetails(userDetails);
        long unreadCount = notificationService.countUnreadNotifications(currentUser);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", unreadCount
        ));
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId) {
        logger.debug("用户[{}]标记通知[{}]为已读", userDetails.getUsername(), notificationId);
        User currentUser = getUserFromUserDetails(userDetails);
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            // 验证通知是否属于当前用户
            if (!notification.getUser().getId().equals(currentUser.getId())) {
                logger.warn("用户[{}]尝试访问不属于自己的通知[{}]", userDetails.getUsername(), notificationId);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "无权操作该通知"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", notification
            ));
        } catch (Exception e) {
            logger.error("标记通知已读失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", STR."标记通知已读失败: \{e.getMessage()}"
            ));
        }
    }
    
    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("用户[{}]标记所有通知为已读", userDetails.getUsername());
        User currentUser = getUserFromUserDetails(userDetails);
        try {
            notificationService.markAllAsRead(currentUser);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "所有通知已标记为已读"
            ));
        } catch (Exception e) {
            logger.error("标记所有通知已读失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", STR."操作失败: \{e.getMessage()}"
            ));
        }
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId) {
        logger.debug("用户[{}]删除通知[{}]", userDetails.getUsername(), notificationId);
        User currentUser = getUserFromUserDetails(userDetails);
        try {
            // 首先检查通知是否属于当前用户
            Notification notification = notificationService.getNotificationById(notificationId);
            if (notification == null) {
                return ResponseEntity.notFound().build();
            }
            if (!notification.getUser().getId().equals(currentUser.getId())) {
                logger.warn("用户[{}]尝试删除不属于自己的通知[{}]", userDetails.getUsername(), notificationId);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "无权删除该通知"
                ));
            }

            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "通知删除成功"
            ));
        } catch (Exception e) {
            logger.error("删除通知失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", STR."删除通知失败: \{e.getMessage()}"
            ));
        }
    }

} 
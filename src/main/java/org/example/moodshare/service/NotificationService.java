package org.example.moodshare.service;

import org.example.moodshare.model.Notification;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    /**
     * 创建新通知
     */
    public Notification createNotification(User user, String message, Notification.NotificationType type, Long relatedEntityId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedEntityId(relatedEntityId);
        return notificationRepository.save(notification);
    }
    
    /**
     * 获取用户的所有通知
     */
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * 获取用户的未读通知
     */
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadStatusOrderByCreatedAtDesc(user, false);
    }
    
    /**
     * 标记通知为已读
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        notification.setReadStatus(true);
        return notificationRepository.save(notification);
    }
    
    /**
     * 标记所有通知为已读
     */
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = getUnreadNotifications(user);
        unreadNotifications.forEach(notification -> notification.setReadStatus(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    /**
     * 获取未读通知数量
     */
    public long countUnreadNotifications(User user) {
        return notificationRepository.countUnreadNotifications(user);
    }
    
    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
} 
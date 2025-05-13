package org.example.moodshare.service;


import org.example.moodshare.model.Notification;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MoodShareApplicationTests {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testNotificationWorkflow() {
        // 创建测试用户
        User user = new User();
        user.setUsername("notificationTestUser");
        user.setPassword("password");
        user.setEmail("test1234@example.com");
        user = userRepository.save(user);

        // 创建通知
        notificationService.createNotification(user, "测试通知1",
                Notification.NotificationType.SYSTEM_MESSAGE, 100L);
        notificationService.createNotification(user, "测试通知2",
                Notification.NotificationType.MOOD_LIKE, 200L);

        // 测试获取通知
        List<Notification> notifications = notificationService.getUserNotifications(user);
        assertEquals(2, notifications.size());

        // 测试获取未读通知
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(user);
        assertEquals(2, unreadNotifications.size());

        // 测试标记通知为已读
        Notification firstNotification = notifications.get(0);
        Notification markedNotification = notificationService.markAsRead(firstNotification.getId());
        assertTrue(markedNotification.isRead());

        // 测试未读通知数量
        long unreadCount = notificationService.countUnreadNotifications(user);
        assertEquals(1, unreadCount);

        // 测试标记所有为已读
        notificationService.markAllAsRead(user);
        unreadNotifications = notificationService.getUnreadNotifications(user);
        assertEquals(0, unreadNotifications.size());

        // 测试删除通知
        notificationService.deleteNotification(firstNotification.getId());
        notifications = notificationService.getUserNotifications(user);
        assertEquals(1, notifications.size());
    }
}
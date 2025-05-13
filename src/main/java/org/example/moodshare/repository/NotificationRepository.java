package org.example.moodshare.repository;

import org.example.moodshare.model.Notification;
import org.example.moodshare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 按创建时间降序获取用户的所有通知
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // 获取用户的已读/未读通知
    List<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, boolean isRead);

    // 计算用户未读通知数量
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = ?1 AND n.isRead = false")
    long countUnreadNotifications(User user);


    //分页查询
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, boolean isRead, Pageable pageable);
}
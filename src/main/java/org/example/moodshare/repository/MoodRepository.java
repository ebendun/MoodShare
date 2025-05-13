package org.example.moodshare.repository;

import org.example.moodshare.model.Mood;
import org.example.moodshare.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    @Query("SELECT m FROM Mood m LEFT JOIN FETCH m.comments LEFT JOIN FETCH m.likedBy ORDER BY m.createdAt DESC")
    List<Mood> findAllWithCommentsAndLikes();
    
    // 按用户查询心情，按创建时间降序排序
    List<Mood> findByUserOrderByCreatedAtDesc(User user);
    
    // 按用户列表查询心情，同时过滤隐私级别
    List<Mood> findByUserInAndPrivacyLevelIn(Set<User> users, List<Mood.PrivacyLevel> privacyLevels);
    
    // 查询有经纬度的心情
    List<Mood> findByPrivacyLevelAndLatitudeIsNotNullAndLongitudeIsNotNull(Mood.PrivacyLevel privacyLevel);
    
    // 获取用户的Feed(包括自己和好友的心情)
    @Query("SELECT m FROM Mood m WHERE " +
           "(m.user.id = :userId AND m.privacyLevel IN ('PUBLIC', 'FRIENDS', 'PRIVATE')) OR " +
           "(m.user.id IN (SELECT f.id FROM User u JOIN u.friends f WHERE u.id = :userId) AND m.privacyLevel IN ('PUBLIC', 'FRIENDS')) " +
           "ORDER BY m.createdAt DESC")
    List<Mood> findFeedForUser(@Param("userId") Long userId, Pageable pageable);
    
    // 按标签查询公开心情
    @Query("SELECT m FROM Mood m WHERE :tag MEMBER OF m.tags AND m.privacyLevel = 'PUBLIC' ORDER BY m.createdAt DESC")
    List<Mood> findByTagAndPublic(@Param("tag") String tag);
    
    // 按心情类型查询
    List<Mood> findByMoodTypeAndPrivacyLevelOrderByCreatedAtDesc(Mood.MoodType moodType, Mood.PrivacyLevel privacyLevel);
}
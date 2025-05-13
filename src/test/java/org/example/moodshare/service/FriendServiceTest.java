package org.example.moodshare.service;

import org.example.moodshare.model.User;
import org.example.moodshare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FriendServiceTest {
    
    @Autowired
    private FriendService friendService;
    
    @Autowired
    private UserRepository userRepository;
    
    private User user1;
    private User user2;
    private User user3;
    
    @BeforeEach
    void setUp() {
        // 清理旧数据
        userRepository.deleteAll();
        
        // 创建测试用户1
        user1 = new User();
        user1.setUsername("friendTestUser1");
        user1.setEmail("friendtest1@example.com");
        user1.setPassword("password");
        user1.setFriends(new HashSet<>());
        user1.setFriendRequests(new HashSet<>());
        user1 = userRepository.save(user1);
        
        // 创建测试用户2
        user2 = new User();
        user2.setUsername("friendTestUser2");
        user2.setEmail("friendtest2@example.com");
        user2.setPassword("password");
        user2.setFriends(new HashSet<>());
        user2.setFriendRequests(new HashSet<>());
        user2 = userRepository.save(user2);
        
        // 创建测试用户3
        user3 = new User();
        user3.setUsername("friendTestUser3");
        user3.setEmail("friendtest3@example.com");
        user3.setPassword("password");
        user3.setFriends(new HashSet<>());
        user3.setFriendRequests(new HashSet<>());
        user3 = userRepository.save(user3);
    }
    
    @Test
    void testSendFriendRequest() {
        // 发送好友请求
        friendService.sendFriendRequest(user1, user2.getId());
        
        // 刷新用户2数据
        User refreshedUser2 = userRepository.findById(user2.getId()).orElseThrow();
        
        // 验证好友请求是否成功添加
        assertTrue(refreshedUser2.getFriendRequests().contains(user1));
    }
    
    @Test
    void testSendFriendRequestToExistingFriend() {
        // 先建立好友关系
        user1.getFriends().add(user2);
        user2.getFriends().add(user1);
        userRepository.save(user1);
        userRepository.save(user2);
        
        // 尝试向已是好友的用户发送请求
        assertThrows(RuntimeException.class, () -> {
            friendService.sendFriendRequest(user1, user2.getId());
        });
    }
    
    @Test
    void testSendDuplicateFriendRequest() {
        // 先发送一次请求
        friendService.sendFriendRequest(user1, user2.getId());
        
        // 尝试再次发送同样的请求
        assertThrows(RuntimeException.class, () -> {
            friendService.sendFriendRequest(user1, user2.getId());
        });
    }
    
    @Test
    void testAcceptFriendRequest() {
        // 发送好友请求
        friendService.sendFriendRequest(user1, user2.getId());
        
        // 接受好友请求
        friendService.acceptFriendRequest(user2, user1.getId());
        
        // 刷新用户数据
        User refreshedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User refreshedUser2 = userRepository.findById(user2.getId()).orElseThrow();
        
        // 验证好友关系已建立
        assertTrue(refreshedUser1.getFriends().contains(refreshedUser2));
        assertTrue(refreshedUser2.getFriends().contains(refreshedUser1));
        
        // 验证好友请求已移除
        assertFalse(refreshedUser2.getFriendRequests().contains(refreshedUser1));
    }
    
    @Test
    void testAcceptNonexistentFriendRequest() {
        // 尝试接受不存在的好友请求
        assertThrows(RuntimeException.class, () -> {
            friendService.acceptFriendRequest(user2, user1.getId());
        });
    }
    
    @Test
    void testRejectFriendRequest() {
        // 发送好友请求
        friendService.sendFriendRequest(user1, user2.getId());
        
        // 拒绝好友请求
        friendService.rejectFriendRequest(user2, user1.getId());
        
        // 刷新用户数据
        User refreshedUser2 = userRepository.findById(user2.getId()).orElseThrow();
        
        // 验证好友请求已被移除
        assertFalse(refreshedUser2.getFriendRequests().contains(user1));
        
        // 验证没有建立好友关系
        assertFalse(refreshedUser2.getFriends().contains(user1));
    }
    
    @Test
    void testGetFriends() {
        // 建立好友关系
        user1.getFriends().add(user2);
        user2.getFriends().add(user1);
        user1.getFriends().add(user3);
        user3.getFriends().add(user1);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        
        // 获取用户1的好友列表
        Set<User> friends = friendService.getFriends(user1);
        
        // 验证好友列表
        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }
    
    @Test
    void testGetFriendRequests() {
        // 发送好友请求
        friendService.sendFriendRequest(user1, user2.getId());
        friendService.sendFriendRequest(user3, user2.getId());
        
        // 获取用户2的好友请求列表
        Set<User> friendRequests = friendService.getFriendRequests(user2);
        
        // 验证好友请求列表
        assertEquals(2, friendRequests.size());
        assertTrue(friendRequests.contains(user1));
        assertTrue(friendRequests.contains(user3));
    }
    
    @Test
    void testRemoveFriend() {
        // 建立好友关系
        user1.getFriends().add(user2);
        user2.getFriends().add(user1);
        userRepository.save(user1);
        userRepository.save(user2);
        
        // 移除好友
        friendService.removeFriend(user1, user2.getId());
        
        // 刷新用户数据
        User refreshedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User refreshedUser2 = userRepository.findById(user2.getId()).orElseThrow();
        
        // 验证好友关系已移除
        assertFalse(refreshedUser1.getFriends().contains(refreshedUser2));
        assertFalse(refreshedUser2.getFriends().contains(refreshedUser1));
    }
}

package org.example.moodshare.service;

import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.dto.MoodResponse;
import org.example.moodshare.model.Mood;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.MoodRepository;
import org.example.moodshare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MoodServiceTest {
    
    @Autowired
    private MoodService moodService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MoodRepository moodRepository;
    
    private User testUser;
    private User friendUser;
    private User strangerUser;
    
    @BeforeEach
    void setUp() {
        // 清理旧数据
        userRepository.deleteAll();
        moodRepository.deleteAll();
        
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("moodTestUser");
        testUser.setEmail("moodtest@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
        
        // 创建好友用户
        friendUser = new User();
        friendUser.setUsername("friendUser");
        friendUser.setEmail("friend@example.com");
        friendUser.setPassword("password");
        friendUser = userRepository.save(friendUser);
        
        // 创建陌生用户
        strangerUser = new User();
        strangerUser.setUsername("strangerUser");
        strangerUser.setEmail("stranger@example.com");
        strangerUser.setPassword("password");
        strangerUser = userRepository.save(strangerUser);
        
        // 建立好友关系
        if (testUser.getFriends() == null) {
            testUser.setFriends(new HashSet<>());
        }
        if (friendUser.getFriends() == null) {
            friendUser.setFriends(new HashSet<>());
        }
        testUser.getFriends().add(friendUser);
        friendUser.getFriends().add(testUser);
        userRepository.save(testUser);
        userRepository.save(friendUser);
    }
    
    @Test
    void testCreateMood() {
        MoodCreateRequest request = new MoodCreateRequest();
        request.setContent("这是一条测试心情");
        request.setEmoji("😊");
        request.setTags(new HashSet<>());
        request.getTags().add("测试");
        request.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        
        MoodResponse response = moodService.createMood(request, testUser.getUsername());
        
        assertNotNull(response);
        assertEquals("这是一条测试心情", response.getContent());
        assertEquals("😊", response.getEmoji());
        assertTrue(response.getTags().contains("测试"));
        assertEquals(Mood.PrivacyLevel.PUBLIC, response.getPrivacyLevel());
        assertEquals(testUser.getId(), response.getUser().getId());
    }
    
    @Test
    void testGetMoodById() {
        // 创建测试心情
        MoodCreateRequest request = new MoodCreateRequest();
        request.setContent("这是一条测试心情");
        request.setEmoji("😊");
        request.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        MoodResponse createdMood = moodService.createMood(request, testUser.getUsername());
        
        // 测试获取心情
        MoodResponse retrievedMood = moodService.getMoodById(createdMood.getId(), testUser.getUsername());
        
        assertNotNull(retrievedMood);
        assertEquals(createdMood.getId(), retrievedMood.getId());
        assertEquals("这是一条测试心情", retrievedMood.getContent());
    }
    
    @Test
    void testGetAllMoods() {
        // 创建公开心情
        MoodCreateRequest request1 = new MoodCreateRequest();
        request1.setContent("公开心情");
        request1.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        moodService.createMood(request1, testUser.getUsername());
        
        // 创建好友可见心情
        MoodCreateRequest request2 = new MoodCreateRequest();
        request2.setContent("好友可见心情");
        request2.setPrivacyLevel(Mood.PrivacyLevel.FRIENDS);
        moodService.createMood(request2, testUser.getUsername());
        
        // 创建私密心情
        MoodCreateRequest request3 = new MoodCreateRequest();
        request3.setContent("私密心情");
        request3.setPrivacyLevel(Mood.PrivacyLevel.PRIVATE);
        moodService.createMood(request3, testUser.getUsername());
        
        // 测试自己获取所有心情
        List<MoodResponse> selfMoods = moodService.getAllMoods(testUser.getUsername(), null, null, null);
        assertEquals(3, selfMoods.size());
        
        // 测试好友获取可见心情
        List<MoodResponse> friendMoods = moodService.getAllMoods(friendUser.getUsername(), null, null, null);
        assertEquals(2, friendMoods.size()); // 应该只能看到公开和好友可见的
        
        // 测试陌生人获取可见心情
        List<MoodResponse> strangerMoods = moodService.getAllMoods(strangerUser.getUsername(), null, null, null);
        assertEquals(1, strangerMoods.size()); // 应该只能看到公开的
    }
    
    @Test
    void testToggleLike() {
        // 创建测试心情
        MoodCreateRequest request = new MoodCreateRequest();
        request.setContent("测试点赞心情");
        request.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        MoodResponse createdMood = moodService.createMood(request, testUser.getUsername());
        
        // 测试点赞
        boolean liked = moodService.toggleLike(createdMood.getId(), friendUser.getUsername());
        assertTrue(liked);
        
        // 验证点赞结果
        MoodResponse moodAfterLike = moodService.getMoodById(createdMood.getId(), friendUser.getUsername());
        assertEquals(1, moodAfterLike.getLikeCount());
        assertTrue(moodAfterLike.isLiked());
        
        // 测试取消点赞
        boolean unliked = moodService.toggleLike(createdMood.getId(), friendUser.getUsername());
        assertFalse(unliked);
        
        // 验证取消点赞结果
        MoodResponse moodAfterUnlike = moodService.getMoodById(createdMood.getId(), friendUser.getUsername());
        assertEquals(0, moodAfterUnlike.getLikeCount());
        assertFalse(moodAfterUnlike.isLiked());
    }
    
    @Test
    void testUpdateMood() {
        // 创建测试心情
        MoodCreateRequest createRequest = new MoodCreateRequest();
        createRequest.setContent("原始内容");
        createRequest.setEmoji("😊");
        createRequest.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        MoodResponse createdMood = moodService.createMood(createRequest, testUser.getUsername());
        
        // 更新心情
        MoodCreateRequest updateRequest = new MoodCreateRequest();
        updateRequest.setContent("更新后内容");
        updateRequest.setEmoji("😄");
        updateRequest.setPrivacyLevel(Mood.PrivacyLevel.FRIENDS);
        MoodResponse updatedMood = moodService.updateMood(createdMood.getId(), updateRequest, testUser.getUsername());
        
        // 验证更新结果
        assertEquals("更新后内容", updatedMood.getContent());
        assertEquals("😄", updatedMood.getEmoji());
        assertEquals(Mood.PrivacyLevel.FRIENDS, updatedMood.getPrivacyLevel());
    }
    
    @Test
    void testUpdateMoodUnauthorized() {
        // 创建测试心情
        MoodCreateRequest createRequest = new MoodCreateRequest();
        createRequest.setContent("原始内容");
        MoodResponse createdMood = moodService.createMood(createRequest, testUser.getUsername());
        
        // 尝试由非发布者更新
        MoodCreateRequest updateRequest = new MoodCreateRequest();
        updateRequest.setContent("尝试更新内容");
        
        // 验证更新失败
        assertThrows(ResponseStatusException.class, () -> {
            moodService.updateMood(createdMood.getId(), updateRequest, friendUser.getUsername());
        });
    }
    
    @Test
    void testDeleteMood() {
        // 创建测试心情
        MoodCreateRequest request = new MoodCreateRequest();
        request.setContent("要删除的心情");
        MoodResponse createdMood = moodService.createMood(request, testUser.getUsername());
        
        // 删除心情
        moodService.deleteMood(createdMood.getId(), testUser.getUsername());
        
        // 验证删除成功
        assertThrows(ResponseStatusException.class, () -> {
            moodService.getMoodById(createdMood.getId(), testUser.getUsername());
        });
    }
    
    @Test
    void testDeleteMoodUnauthorized() {
        // 创建测试心情
        MoodCreateRequest request = new MoodCreateRequest();
        request.setContent("要删除的心情");
        MoodResponse createdMood = moodService.createMood(request, testUser.getUsername());
        
        // 尝试由非发布者删除
        assertThrows(ResponseStatusException.class, () -> {
            moodService.deleteMood(createdMood.getId(), friendUser.getUsername());
        });
        
        // 验证心情仍然存在
        MoodResponse mood = moodService.getMoodById(createdMood.getId(), testUser.getUsername());
        assertNotNull(mood);
    }
    
    @Test
    void testGetFriendsMoods() {
        // 创建测试用户的心情
        MoodCreateRequest request1 = new MoodCreateRequest();
        request1.setContent("测试用户的心情");
        request1.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        moodService.createMood(request1, testUser.getUsername());
        
        // 创建好友的公开心情
        MoodCreateRequest request2 = new MoodCreateRequest();
        request2.setContent("好友的公开心情");
        request2.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        moodService.createMood(request2, friendUser.getUsername());
        
        // 创建好友的好友可见心情
        MoodCreateRequest request3 = new MoodCreateRequest();
        request3.setContent("好友的好友可见心情");
        request3.setPrivacyLevel(Mood.PrivacyLevel.FRIENDS);
        moodService.createMood(request3, friendUser.getUsername());
        
        // 创建好友的私密心情
        MoodCreateRequest request4 = new MoodCreateRequest();
        request4.setContent("好友的私密心情");
        request4.setPrivacyLevel(Mood.PrivacyLevel.PRIVATE);
        moodService.createMood(request4, friendUser.getUsername());
        
        // 获取好友心情
        List<MoodResponse> friendsMoods = moodService.getFriendsMoods(testUser.getUsername());
        
        // 验证只能获取到好友的公开和好友可见心情
        assertEquals(2, friendsMoods.size());
        assertTrue(friendsMoods.stream().anyMatch(m -> m.getContent().equals("好友的公开心情")));
        assertTrue(friendsMoods.stream().anyMatch(m -> m.getContent().equals("好友的好友可见心情")));
        assertFalse(friendsMoods.stream().anyMatch(m -> m.getContent().equals("好友的私密心情")));
    }
}

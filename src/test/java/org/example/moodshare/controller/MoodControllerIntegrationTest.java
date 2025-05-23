package org.example.moodshare.controller;

import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.model.Mood;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.MoodRepository;
import org.example.moodshare.repository.UserRepository;
import org.example.moodshare.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MoodControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MoodRepository moodRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private User testUser;
    private User friendUser;
    private String testUserToken;
    private String friendUserToken;
    
    @BeforeEach
    void setUp() {
        // 清理旧数据
        moodRepository.deleteAll();
        userRepository.deleteAll();
        
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("controllerTestUser");
        testUser.setEmail("controllertest@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setFriends(new HashSet<>());
        testUser = userRepository.save(testUser);
        
        // 创建好友用户
        friendUser = new User();
        friendUser.setUsername("controllerFriendUser");
        friendUser.setEmail("controllerfriend@example.com");
        friendUser.setPassword(passwordEncoder.encode("password"));
        friendUser.setFriends(new HashSet<>());
        friendUser = userRepository.save(friendUser);
        
        // 建立好友关系
        testUser.getFriends().add(friendUser);
        friendUser.getFriends().add(testUser);
        userRepository.save(testUser);
        userRepository.save(friendUser);
        
        // 生成JWT令牌
        testUserToken = jwtUtil.generateToken(testUser.getUsername());
        friendUserToken = jwtUtil.generateToken(friendUser.getUsername());
    }
    
    @Test
    void testCreateMood() throws Exception {
        MoodCreateRequest request = new MoodCreateRequest();
        request.setContent("集成测试创建的心情");
        request.setEmoji("😊");
        Set<String> tags = new HashSet<>();
        tags.add("测试");
        tags.add("集成");
        request.setTags(tags);
        request.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
          mockMvc.perform(post("/moods")
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("集成测试创建的心情")))
                .andExpect(jsonPath("$.emoji", is("😊")))
                .andExpect(jsonPath("$.tags", hasItems("测试", "集成")))
                .andExpect(jsonPath("$.privacyLevel", is("PUBLIC")))
                .andExpect(jsonPath("$.user.username", is("controllerTestUser")));
    }
    
    @Test
    void testGetAllMoods() throws Exception {
        // 创建测试心情
        Mood mood1 = new Mood();
        mood1.setContent("公开心情");
        mood1.setUser(testUser);
        mood1.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        moodRepository.save(mood1);
        
        Mood mood2 = new Mood();
        mood2.setContent("好友可见心情");
        mood2.setUser(testUser);
        mood2.setPrivacyLevel(Mood.PrivacyLevel.FRIENDS);
        moodRepository.save(mood2);
          // 测试获取所有心情
        mockMvc.perform(get("/moods")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].content", hasItems("公开心情", "好友可见心情")));
    }
    
    @Test
    void testGetMoodById() throws Exception {
        // 创建测试心情
        Mood mood = new Mood();
        mood.setContent("测试获取单个心情");
        mood.setUser(testUser);
        mood.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        mood = moodRepository.save(mood);
          // 测试获取单个心情
        mockMvc.perform(get("/moods/" + mood.getId())
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("测试获取单个心情")))
                .andExpect(jsonPath("$.user.username", is("controllerTestUser")));
    }
    
    @Test
    void testToggleLike() throws Exception {
        // 创建测试心情
        Mood mood = new Mood();
        mood.setContent("测试点赞心情");
        mood.setUser(testUser);
        mood.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        mood.setLikedBy(new HashSet<>());
        mood = moodRepository.save(mood);
          // 测试点赞
        mockMvc.perform(post("/moods/" + mood.getId() + "/like")
                .header("Authorization", "Bearer " + friendUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked", is(true)));
        
        // 测试再次点赞取消
        mockMvc.perform(post("/moods/" + mood.getId() + "/like")
                .header("Authorization", "Bearer " + friendUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked", is(false)));
    }
    
    @Test
    void testUpdateMood() throws Exception {
        // 创建测试心情
        Mood mood = new Mood();
        mood.setContent("原始内容");
        mood.setUser(testUser);
        mood.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        mood = moodRepository.save(mood);
        
        // 更新请求
        MoodCreateRequest updateRequest = new MoodCreateRequest();
        updateRequest.setContent("更新后的内容");
        updateRequest.setEmoji("🎉");
        updateRequest.setPrivacyLevel(Mood.PrivacyLevel.FRIENDS);
          // 测试更新心情
        mockMvc.perform(put("/moods/" + mood.getId())
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("更新后的内容")))
                .andExpect(jsonPath("$.emoji", is("🎉")))
                .andExpect(jsonPath("$.privacyLevel", is("FRIENDS")));
    }
    
    @Test
    void testUpdateMoodUnauthorized() throws Exception {
        // 创建测试心情
        Mood mood = new Mood();
        mood.setContent("测试未授权更新");
        mood.setUser(testUser);
        mood.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        mood = moodRepository.save(mood);
        
        // 更新请求
        MoodCreateRequest updateRequest = new MoodCreateRequest();
        updateRequest.setContent("尝试未授权更新");
          // 测试未授权更新
        mockMvc.perform(put("/moods/" + mood.getId())
                .header("Authorization", "Bearer " + friendUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testDeleteMood() throws Exception {
        // 创建测试心情
        Mood mood = new Mood();
        mood.setContent("测试删除心情");
        mood.setUser(testUser);
        mood.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        mood = moodRepository.save(mood);
          // 测试删除心情
        mockMvc.perform(delete("/moods/" + mood.getId())
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isNoContent());
        
        // 验证已删除
        mockMvc.perform(get("/moods/" + mood.getId())
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteMoodUnauthorized() throws Exception {
        // 创建测试心情
        Mood mood = new Mood();
        mood.setContent("测试未授权删除");
        mood.setUser(testUser);
        mood.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        mood = moodRepository.save(mood);
        
        // 测试未授权删除
        mockMvc.perform(delete("/moods/" + mood.getId())
                .header("Authorization", "Bearer " + friendUserToken))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testGetFeed() throws Exception {
        // 创建测试心情
        Mood mood1 = new Mood();
        mood1.setContent("用户自己的心情");
        mood1.setUser(testUser);
        mood1.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        moodRepository.save(mood1);
        
        Mood mood2 = new Mood();
        mood2.setContent("好友的公开心情");
        mood2.setUser(friendUser);
        mood2.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        moodRepository.save(mood2);
        
        // 测试获取Feed
        mockMvc.perform(get("/moods/feed")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].content", hasItems("用户自己的心情", "好友的公开心情")));
    }
}

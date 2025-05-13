package org.example.moodshare.service;

import org.example.moodshare.dto.CommentRequest;
import org.example.moodshare.dto.CommentResponse;
import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.dto.MoodResponse;
import org.example.moodshare.model.Mood;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.CommentRepository;
import org.example.moodshare.repository.MoodRepository;
import org.example.moodshare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CommentServiceTest {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private MoodService moodService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MoodRepository moodRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    private User testUser;
    private User secondUser;
    private Mood testMood;
    
    @BeforeEach
    void setUp() {
        // 清理旧数据
        commentRepository.deleteAll();
        moodRepository.deleteAll();
        userRepository.deleteAll();
        
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("commentTestUser");
        testUser.setEmail("commenttest@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
        
        // 创建第二个用户
        secondUser = new User();
        secondUser.setUsername("secondUser");
        secondUser.setEmail("second@example.com");
        secondUser.setPassword("password");
        secondUser = userRepository.save(secondUser);
        
        // 创建测试心情
        MoodCreateRequest moodRequest = new MoodCreateRequest();
        moodRequest.setContent("测试评论的心情");
        moodRequest.setPrivacyLevel(Mood.PrivacyLevel.PUBLIC);
        MoodResponse moodResponse = moodService.createMood(moodRequest, testUser.getUsername());
        
        // 获取已保存的心情
        testMood = moodRepository.findById(moodResponse.getId())
                .orElseThrow(() -> new RuntimeException("测试心情创建失败"));
    }
    
    @Test
    void testAddComment() {
        // 创建评论请求
        CommentRequest request = new CommentRequest();
        request.setContent("这是一条测试评论");
        
        // 添加评论
        CommentResponse response = commentService.addComment(testMood.getId(), request, secondUser.getUsername());
        
        // 验证评论创建成功
        assertNotNull(response);
        assertEquals("这是一条测试评论", response.getContent());
        assertEquals(secondUser.getUsername(), response.getUsername());
        
        // 验证通过心情获取评论
        List<CommentResponse> comments = commentService.getCommentsByMoodId(testMood.getId());
        assertEquals(1, comments.size());
        assertEquals("这是一条测试评论", comments.get(0).getContent());
    }
    
    @Test
    void testGetCommentsByMoodId() {
        // 添加多条评论
        CommentRequest request1 = new CommentRequest();
        request1.setContent("评论1");
        commentService.addComment(testMood.getId(), request1, testUser.getUsername());
        
        CommentRequest request2 = new CommentRequest();
        request2.setContent("评论2");
        commentService.addComment(testMood.getId(), request2, secondUser.getUsername());
        
        // 获取心情的所有评论
        List<CommentResponse> comments = commentService.getCommentsByMoodId(testMood.getId());
        
        // 验证评论数量和内容
        assertEquals(2, comments.size());
        assertTrue(comments.stream().anyMatch(c -> c.getContent().equals("评论1")));
        assertTrue(comments.stream().anyMatch(c -> c.getContent().equals("评论2")));
    }
    
    @Test
    void testDeleteCommentByAuthor() {
        // 添加评论
        CommentRequest request = new CommentRequest();
        request.setContent("要删除的评论");
        CommentResponse response = commentService.addComment(testMood.getId(), request, secondUser.getUsername());
        
        // 由评论作者删除评论
        commentService.deleteComment(testMood.getId(), response.getId(), secondUser.getUsername());
        
        // 验证评论已被删除
        List<CommentResponse> comments = commentService.getCommentsByMoodId(testMood.getId());
        assertEquals(0, comments.size());
    }
    
    @Test
    void testDeleteCommentByMoodOwner() {
        // 添加评论
        CommentRequest request = new CommentRequest();
        request.setContent("要删除的评论");
        CommentResponse response = commentService.addComment(testMood.getId(), request, secondUser.getUsername());
        
        // 由心情所有者删除评论
        commentService.deleteComment(testMood.getId(), response.getId(), testUser.getUsername());
        
        // 验证评论已被删除
        List<CommentResponse> comments = commentService.getCommentsByMoodId(testMood.getId());
        assertEquals(0, comments.size());
    }
    
    @Test
    void testDeleteCommentUnauthorized() {
        // 添加评论
        CommentRequest request = new CommentRequest();
        request.setContent("评论1");
        CommentResponse response = commentService.addComment(testMood.getId(), request, testUser.getUsername());
        
        // 创建第三个用户
        User thirdUser = new User();
        thirdUser.setUsername("thirdUser");
        thirdUser.setEmail("third@example.com");
        thirdUser.setPassword("password");
        thirdUser = userRepository.save(thirdUser);
        
        // 尝试由非评论作者和非心情所有者删除评论
        User finalThirdUser = thirdUser;
        assertThrows(ResponseStatusException.class, () -> {
            commentService.deleteComment(testMood.getId(), response.getId(), finalThirdUser.getUsername());
        });
        
        // 验证评论仍然存在
        List<CommentResponse> comments = commentService.getCommentsByMoodId(testMood.getId());
        assertEquals(1, comments.size());
    }
    
    @Test
    void testDeleteNonexistentComment() {
        // 尝试删除不存在的评论
        assertThrows(ResponseStatusException.class, () -> {
            commentService.deleteComment(testMood.getId(), 9999L, testUser.getUsername());
        });
    }
}

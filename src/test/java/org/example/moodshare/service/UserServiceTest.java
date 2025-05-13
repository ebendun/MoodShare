package org.example.moodshare.service;

import org.example.moodshare.dto.UserProfileUpdateRequest;
import org.example.moodshare.dto.UserRegistrationRequest;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User testUser;
    private String testUsername = "testUserService";
    private String testEmail = "testUserService@example.com";
    private String testPassword = "password123";
    
    @BeforeEach
    void setUp() {
        // 清理旧数据
        userRepository.findByUsername(testUsername)
                .ifPresent(user -> userRepository.delete(user));
        
        userRepository.findByEmail(testEmail)
                .ifPresent(user -> userRepository.delete(user));
          // 创建测试用户
        // 直接使用参数调用 registerUser 方法
        userService.registerUser(testUsername, testEmail, testPassword);
        
        // 获取已保存的用户
        testUser = userRepository.findByUsername(testUsername)
                .orElseThrow(() -> new RuntimeException("测试用户创建失败"));
    }
      @Test
    void testRegisterUserSuccess() {
        // 准备新用户数据
        String newUsername = "newTestUser";
        String newEmail = "newtest@example.com";
        String newPassword = "newpassword123";
        
        // 注册新用户
        User user = userService.registerUser(newUsername, newEmail, newPassword);
        
        // 验证用户创建成功
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(newUsername, user.getUsername());
        assertEquals(newEmail, user.getEmail());
        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
        assertEquals(User.ROLE_USER, user.getRole());
    }
      @Test
    void testRegisterUserDuplicateUsername() {
        // 准备重复用户名的请求
        String duplicateUsername = testUsername; // 使用已存在的用户名
        String newEmail = "another@example.com";
        String newPassword = "password123";
        
        // 验证注册失败
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(duplicateUsername, newEmail, newPassword);
        });
    }
      @Test
    void testRegisterUserDuplicateEmail() {
        // 准备重复邮箱的请求
        String newUsername = "anotherUsername";
        String duplicateEmail = testEmail; // 使用已存在的邮箱
        String newPassword = "password123";
        
        // 验证注册失败
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(newUsername, duplicateEmail, newPassword);
        });
    }
    
    @Test
    void testGetUserByUsername() {
        User user = userService.getUserByUsername(testUsername);
        
        assertNotNull(user);
        assertEquals(testUsername, user.getUsername());
        assertEquals(testEmail, user.getEmail());
    }
    
    @Test
    void testGetUserByUsernameNotFound() {
        assertThrows(ResponseStatusException.class, () -> {
            userService.getUserByUsername("nonexistentUser");
        });
    }
      @Test
    void testUpdateUserProfile() {
        String bio = "这是一个测试简介";
        String profilePicture = "https://example.com/profile.jpg";
        
        User updatedUser = userService.updateProfile(testUser, bio, profilePicture);
        
        assertNotNull(updatedUser);
        assertEquals(bio, updatedUser.getBio());
        assertEquals(profilePicture, updatedUser.getProfilePicture());
    }
    
    @Test
    void testChangePassword() {
        String newPassword = "newPassword123";
        
        User updatedUser = userService.changePassword(testUser.getUsername(), testPassword, newPassword);
        
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }
    
    @Test
    void testChangePasswordWrongOldPassword() {
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword123";
        
        assertThrows(BadCredentialsException.class, () -> {
            userService.changePassword(testUser.getUsername(), wrongPassword, newPassword);
        });
    }
}

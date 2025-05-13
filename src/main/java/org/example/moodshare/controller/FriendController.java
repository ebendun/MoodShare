package org.example.moodshare.controller;

import org.example.moodshare.model.User;
import org.example.moodshare.service.FriendService;
import org.example.moodshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取好友列表
     */
    @GetMapping
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
        }
        
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        Set<User> friends = friendService.getFriends(currentUser);
        
        // 将朋友列表转换为简单信息，避免循环引用
        return getResponseEntity(friends);
    }

    private ResponseEntity<?> getResponseEntity(Set<User> friends) {
        Set<Map<String, Object>> friendInfos = friends.stream().map(friend -> {
            Map<String, Object> friendInfo = new HashMap<>();
            friendInfo.put("id", friend.getId());
            friendInfo.put("username", friend.getUsername());
            friendInfo.put("profilePicture", friend.getProfilePicture());
            return friendInfo;
        }).collect(Collectors.toSet());

        return ResponseEntity.ok(friendInfos);
    }

    /**
     * 获取好友请求列表
     */
    @GetMapping("/requests")
    public ResponseEntity<?> getFriendRequests(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
        }
        
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        Set<User> requests = friendService.getFriendRequests(currentUser);
        
        // 将请求列表转换为简单信息
        return getResponseEntity(requests);
    }
    
    /**
     * 发送好友请求
     */
    @PostMapping("/request/{userId}")
    public ResponseEntity<?> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
        }
        
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        friendService.sendFriendRequest(currentUser, userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 接受好友请求
     */
    @PostMapping("/accept/{userId}")
    public ResponseEntity<?> acceptFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
        }
        
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        friendService.acceptFriendRequest(currentUser, userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 拒绝好友请求
     */
    @PostMapping("/reject/{userId}")
    public ResponseEntity<?> rejectFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
        }
        
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        friendService.rejectFriendRequest(currentUser, userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 删除好友
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> removeFriend(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证，请先登录");
        }
        
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        friendService.removeFriend(currentUser, friendId);
        return ResponseEntity.ok().build();
    }
} 
package org.example.moodshare.controller;

import org.example.moodshare.dto.FriendsApiResponse;
import org.example.moodshare.model.User;
import org.example.moodshare.service.FriendService;
import org.example.moodshare.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friends")
public class FriendController {


    private final FriendService friendService;
    private final UserService userService;

    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser(UserDetails userDetails) {
        if(userDetails == null) {
            throw new RuntimeException( "未认证，请先登录");
        }
        return userService.getUserByUsername(userDetails.getUsername());
    }

    /**
     * 获取好友列表
     */
    @GetMapping
    public ResponseEntity<FriendsApiResponse<?>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        Set<User> friends = friendService.getFriends(currentUser);

        return ResponseEntity.ok(new FriendsApiResponse<>(
                true,
                "获取好友列表成功",
                convertUsersToSimpleInfo(friends)
        ));
    }
    /**
     * 将用户集合转换为简单信息
     */
    private Set<Map<String, Object>> convertUsersToSimpleInfo(Set<User> users) {
        return users.stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("profilePicture", user.getProfilePicture());
            return userInfo;
        }).collect(Collectors.toSet());
    }

        /**
         * 获取好友请求列表
         */
        @GetMapping("/requests")
        public ResponseEntity<FriendsApiResponse<?>> getFriendRequests (@AuthenticationPrincipal UserDetails userDetails)
        {
            User currentUser = getCurrentUser(userDetails);
            Set<User> requests = friendService.getFriendRequests(currentUser);


            return ResponseEntity.ok(new FriendsApiResponse<>(
                    true,
                    "获取好友请求列表成功",
                    convertUsersToSimpleInfo(requests)
            ));
        }

    
    /**
     * 发送好友请求
     */
        @PostMapping("/request/{userId}")
        public ResponseEntity<FriendsApiResponse<?>> sendFriendRequest(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long userId) {

            User currentUser = getCurrentUser(userDetails);
            try {
                friendService.sendFriendRequest(currentUser, userId);
                return ResponseEntity.ok(new FriendsApiResponse<>(true, "好友请求已发送", null));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(new FriendsApiResponse<>(false, e.getMessage(), null));
            }
        }

        /**
         * 接受好友请求
         */
        @PostMapping("/accept/{userId}")
        public ResponseEntity<FriendsApiResponse<?>> acceptFriendRequest(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long userId) {

            User currentUser = getCurrentUser(userDetails);
            try {
                friendService.acceptFriendRequest(currentUser, userId);
                return ResponseEntity.ok(new FriendsApiResponse<>(true, "已接受好友请求", null));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(new FriendsApiResponse<>(false, e.getMessage(), null));
            }
        }


        /**
         * 拒绝好友请求
         */
        @PostMapping("/reject/{userId}")
        public ResponseEntity<FriendsApiResponse<?>> rejectFriendRequest(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long userId) {

            User currentUser = getCurrentUser(userDetails);
            try {
                friendService.rejectFriendRequest(currentUser, userId);
                return ResponseEntity.ok(new FriendsApiResponse<>(true, "已拒绝好友请求", null));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(new FriendsApiResponse<>(false, e.getMessage(), null));
            }
        }

        /**
         * 删除好友
         */
        @DeleteMapping("/{friendId}")
        public ResponseEntity<FriendsApiResponse<?>> removeFriend(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long friendId) {

            User currentUser = getCurrentUser(userDetails);
            try {
                friendService.removeFriend(currentUser, friendId);
                return ResponseEntity.ok(new FriendsApiResponse<>(true, "好友已删除", null));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(new FriendsApiResponse<>(false, e.getMessage(), null));
            }
        }
} 
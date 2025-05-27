package org.example.moodshare.service;

import org.example.moodshare.model.Notification;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;


@Service
public class FriendService {


    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FriendService(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    
    /**
     * 发送好友请求
     */
    @Transactional
    public void sendFriendRequest(User sender, Long receiverId) {
        System.out.println("发送好友请求: 发送者ID = " + sender.getId() + ", 接收者ID = " + receiverId);
        
        //防止自己添加自己
        if (sender.getId().equals(receiverId)) {
            System.out.println("错误: 不能添加自己为好友");
            throw new RuntimeException("不能添加自己为好友");
        }

        System.out.println("开始查找接收者用户...");
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {
                    System.out.println("错误: 找不到接收者用户, ID = " + receiverId);
                    return new RuntimeException("用户不存在");
                });
        System.out.println("成功找到接收者用户: " + receiver.getUsername());
        
        // 检查是否已经是好友
        if (sender.getFriends().contains(receiver)) {
            System.out.println("错误: 已经是好友了");
            throw new RuntimeException("你们已经是好友了");
        }

        // 检查是否已经发送过好友请求
        if (receiver.getFriendRequests().contains(sender)) {
            throw new RuntimeException("已经发送过好友请求，请等待对方回应");
        }

        //检查对方是否已经发送过请求，如果是，则直接建立好友关系
        if (sender.getFriendRequests().contains(receiver)) {
            System.out.println("发现对方已发送过好友请求，自动接受");
            // 建立双向好友关系（注意：这里是sender接受receiver的请求）
            acceptFriendRequest(sender, receiver.getId());
            return;
        }

        
        // 添加好友请求
        receiver.getFriendRequests().add(sender);
        userRepository.save(receiver);
        
        // 创建通知
        notificationService.createNotification(
                receiver,
                STR."\{sender.getUsername()}向你发送了好友请求",
                Notification.NotificationType.FRIEND_REQUEST,
                sender.getId()
        );
    }
    
    /**
     * 接受好友请求
     */
    @Transactional
    public void acceptFriendRequest(User receiver, Long senderId) {
        System.out.println("接受好友请求: 接收者ID = " + receiver.getId() + ", 发送者ID = " + senderId);
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    System.out.println("错误: 找不到发送请求的用户, ID = " + senderId);
                    return new RuntimeException("用户不存在");
                });
        System.out.println("成功找到发送请求的用户: " + sender.getUsername());
        
        // 检查是否有该用户的好友请求
        if (!receiver.getFriendRequests().contains(sender)) {
            System.out.println("错误: 没有找到来自用户 " + sender.getUsername() + " 的好友请求");
            throw new RuntimeException("没有找到该好友请求");
        }
        System.out.println("找到好友请求，开始建立好友关系");
        
        // 建立双向好友关系
        receiver.getFriends().add(sender);
        sender.getFriends().add(receiver);
        
        // 移除好友请求
        receiver.getFriendRequests().remove(sender);
        
        userRepository.save(receiver);
        userRepository.save(sender);
        System.out.println("好友关系建立成功");
        
        // 创建通知
        notificationService.createNotification(
                sender,
                STR."\{receiver.getUsername()}接受了你的好友请求",
                Notification.NotificationType.FRIEND_ACCEPT,
                receiver.getId()
        );
    }
    
    /**
     * 拒绝好友请求
     */
    @Transactional
    public void rejectFriendRequest(User receiver, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否有该用户的好友请求
        if (!receiver.getFriendRequests().contains(sender)) {
            throw new RuntimeException("没有找到该好友请求");
        }
        
        // 移除好友请求
        receiver.getFriendRequests().remove(sender);
        userRepository.save(receiver);
    }
    
    /**
     * 获取好友列表
     */
    public Set<User> getFriends(User user) {
        return user.getFriends();
    }
    
    /**
     * 获取好友请求列表
     */
    public Set<User> getFriendRequests(User user) {
        return user.getFriendRequests();
    }
    
    /**
     * 删除好友
     */
    @Transactional
    public void removeFriend(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否是好友
        if (!user.getFriends().contains(friend)) {
            throw new RuntimeException("该用户不是你的好友");
        }
        
        // 移除双向好友关系
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        
        userRepository.save(user);
        userRepository.save(friend);
    }
} 
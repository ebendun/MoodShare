package org.example.moodshare.service;
import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.dto.MoodResponse;
import org.example.moodshare.model.Mood;
import org.example.moodshare.model.Notification;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.MoodRepository;
import org.example.moodshare.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.example.moodshare.model.Comment;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class MoodService {


    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MoodService(MoodRepository moodRepository, UserRepository userRepository, NotificationService notificationService) {
        this.moodRepository = moodRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public MoodResponse createMood(MoodCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        Mood mood = new Mood();
        mood.setUser(user);
        // 设置新增的字段
        return getMoodResponse(request, username, mood);
    }
    
    /**
     * 根据用户名获取用户ID
     */
    @Transactional(readOnly = true)
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
    }
    /**
     *设置心情实体
     */
    private  void prepareMoodEntity(Mood mood, MoodCreateRequest request) {
        mood.setContent(request.getContent());
        mood.setEmoji(request.getEmoji());
        mood.setTags(request.getTags());
        mood.setPrivacyLevel(request.getPrivacyLevel());
        mood.setLocation(request.getLocation());
        mood.setLatitude(request.getLatitude());
        mood.setLongitude(request.getLongitude());
        mood.setMoodType(request.getMoodType());
        mood.setWeather(request.getWeather());
    }

    /**
     * 获取心情响应
     */
    private MoodResponse getMoodResponse(MoodCreateRequest request, String username, Mood mood) {
        prepareMoodEntity(mood, request);
        mood = moodRepository.save(mood);
        return convertToMoodResponse(mood, username);
    }

    @Transactional
    public MoodResponse updateMood(Long id, MoodCreateRequest request, String username) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 检查是否是心情发布者
        if (!mood.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "没有权限更新这条心情");
        }

        return getMoodResponse(request, username, mood);
    }

    @Transactional(readOnly = true)
    public List<MoodResponse> getAllMoods(String currentUsername, Mood.PrivacyLevel privacyLevel, Mood.MoodType moodType, String location) {
        User currentUser;
        if (currentUsername != null) {
            currentUser = userRepository.findByUsername(currentUsername)
                    .orElse(null); // Use orElse(null) instead of throwing exception
        } else {
            currentUser = null;
        }

        List<Mood> moods = moodRepository.findAllWithCommentsAndLikes();
        
        // 根据条件过滤心情
        List<Mood> filteredMoods = moods.stream()
                .filter(mood -> {
                    // For unauthenticated users, only show PUBLIC moods
                    if (currentUser == null) {
                        return mood.getPrivacyLevel() == Mood.PrivacyLevel.PUBLIC;
                    } else {
                        return hasAccessToMood(mood, currentUser);
                    }
                })
                .filter(mood -> privacyLevel == null || mood.getPrivacyLevel() == privacyLevel)
                .filter(mood -> moodType == null || mood.getMoodType() == moodType)
                .filter(mood -> location == null || (mood.getLocation() != null && mood.getLocation().contains(location)))
                .toList();

        return filteredMoods.stream()
                .map(mood -> convertToMoodResponse(mood, currentUsername))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MoodResponse> getFeed(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        
        // 这里需要在MoodRepository中添加相应的查询方法
        List<Mood> feed = moodRepository.findFeedForUser(user.getId(), pageable);
        
        return feed.stream()
                .filter(mood -> hasAccessToMood(mood, user))
                .map(mood -> convertToMoodResponse(mood, username))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MoodResponse> getFriendsMoods(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        
        // 获取好友列表
        Set<User> friends = user.getFriends();
        
        // 获取好友的心情
        List<Mood> friendsMoods = moodRepository.findByUserInAndPrivacyLevelIn(
                friends, 
                Arrays.asList(Mood.PrivacyLevel.PUBLIC, Mood.PrivacyLevel.FRIENDS)
        );
        
        return friendsMoods.stream()
                .map(mood -> convertToMoodResponse(mood, username))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MoodResponse> getUserMoods(Long userId, String currentUsername) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
                
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "当前用户不存在"));
        
        // 判断当前用户与目标用户的关系，决定可见性
        boolean isFriend = currentUser.getFriends().contains(targetUser);
        boolean isSelf = currentUser.getId().equals(targetUser.getId());
        
        List<Mood> userMoods = moodRepository.findByUserOrderByCreatedAtDesc(targetUser);
        
        return userMoods.stream()
                .filter(mood -> {
                    // 自己可以看所有自己的心情
                    if (isSelf) return true;
                    // 朋友可以看公开和朋友可见的心情
                    if (isFriend) return mood.getPrivacyLevel() != Mood.PrivacyLevel.PRIVATE;
                    // 其他人只能看公开的心情
                    return mood.getPrivacyLevel() == Mood.PrivacyLevel.PUBLIC;
                })
                .map(mood -> convertToMoodResponse(mood, currentUsername))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MoodResponse getMoodById(Long id, String currentUsername) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));
                
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
                
        // 检查用户是否有权限查看该心情
        if (!hasAccessToMood(mood, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权查看此心情");
        }
        
        return convertToMoodResponse(mood, currentUsername);
    }

    @Transactional
    public boolean toggleLike(Long moodId, String username) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
                
        // 检查用户是否有权限查看该心情
        if (!hasAccessToMood(mood, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作此心情");
        }

        // 使用新的集合进行操作
        Set<User> likedUsers = new HashSet<>(mood.getLikedBy());

        boolean result;
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
            result = false;
        } else {
            likedUsers.add(user);
            // 给作者发送通知
            if(!mood.getUser().getId().equals(user.getId())) {
                notificationService.createNotification(
                        mood.getUser(),
                        STR."\{user.getUsername()} 点赞了你的心情",
                        Notification.NotificationType.MOOD_LIKE,
                        mood.getId()
                );
            }
            result = true;
        }

        // 设置回原对象
        mood.setLikedBy(likedUsers);
        moodRepository.save(mood);
        return result;
    }
    
    @Transactional
    public MoodResponse updatePrivacy(Long moodId, Mood.PrivacyLevel privacyLevel, String username) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));
                
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
                
        // 只有自己能修改自己的心情隐私设置
        if (!mood.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此心情");
        }
        
        mood.setPrivacyLevel(privacyLevel);
        mood = moodRepository.save(mood);
        
        return convertToMoodResponse(mood, username);
    }
    
    @Transactional(readOnly = true)
    public List<MoodResponse> getNearbyMoods(Double latitude, Double longitude, Double radiusKm, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 获取附近的公开心情，这里可以使用地理空间查询
        // 简化版本：只过滤有经纬度的心情，然后计算距离
        List<Mood> allMoods = moodRepository.findByPrivacyLevelAndLatitudeIsNotNullAndLongitudeIsNotNull(
                Mood.PrivacyLevel.PUBLIC);
                
        // 获取当前用户的好友
        Set<User> friends = user.getFriends();
                
        // 过滤符合条件的心情
        List<Mood> nearbyMoods = allMoods.stream()
                .filter(mood -> {
                    // 检查是否是好友的心情，如果是则可以看到好友可见的心情
                    boolean isFriendMood = friends.contains(mood.getUser()) && 
                                           mood.getPrivacyLevel() == Mood.PrivacyLevel.FRIENDS;
                    
                    // 检查是否是自己的心情
                    boolean isOwnMood = mood.getUser().getId().equals(user.getId());
                    
                    // 检查是否是公开心情
                    boolean isPublicMood = mood.getPrivacyLevel() == Mood.PrivacyLevel.PUBLIC;
                    
                    // 必须是公开的、好友可见的(如果是好友)或自己的心情
                    return isPublicMood || isFriendMood || isOwnMood;
                })
                .filter(mood -> calculateDistance(latitude, longitude, mood.getLatitude(), mood.getLongitude()) <= radiusKm)
                .toList();
                
        return nearbyMoods.stream()
                .map(mood -> convertToMoodResponse(mood, username))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMood(Long id, String username) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 检查是否是心情发布者或管理员
        if (!mood.getUser().getUsername().equals(username) && !isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此心情");
        }

        moodRepository.delete(mood);
    }

    private boolean isAdmin(User user) {
        return user.isAdmin();
    }
    
    // 检查用户是否有权限访问心情
    private boolean hasAccessToMood(Mood mood, User user) {
        // 自己的心情都可以看
        if (mood.getUser().getId().equals(user.getId())) {
            return true;
        }
        
        // 管理员可以看到所有心情
        if (isAdmin(user)) {
            return true;
        }
        
        // 公开心情所有人可见
        if (mood.getPrivacyLevel() == Mood.PrivacyLevel.PUBLIC) {
            return true;
        }
        
        // 好友可见的心情，只有好友可以看
        if (mood.getPrivacyLevel() == Mood.PrivacyLevel.FRIENDS) {
            return user.getFriends().contains(mood.getUser());
        }
        
        // 私密心情只有自己可以看(前面已判断)
        return false;
    }
    
    // 计算两点之间的距离（公里）
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 使用Haversine公式计算球面两点间的距离
        final int R = 6371; // 地球半径，单位：公里
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    private MoodResponse convertToMoodResponse(Mood mood, String currentUsername) {
        // 处理未认证的用户访问，currentUsername可能为null
        User currentUser = null;
        if (currentUsername != null) {
            currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        }


        MoodResponse response = new MoodResponse();
        response.setId(mood.getId());
        response.setContent(mood.getContent());
        response.setEmoji(mood.getEmoji());
        response.setTags(mood.getTags());

        // 设置点赞信息
        response.setLikeCount(mood.getLikeCount());
        boolean liked = currentUser != null && mood.getLikedBy().contains(currentUser);
        response.setLiked(liked);
        
        // 设置新增字段
        response.setPrivacyLevel(mood.getPrivacyLevel());
        response.setLocation(mood.getLocation());
        response.setLatitude(mood.getLatitude());
        response.setLongitude(mood.getLongitude());
        response.setMoodType(mood.getMoodType());
        response.setWeather(mood.getWeather());

        //添加用户信息
        MoodResponse.UserDto userDto = new MoodResponse.UserDto();
        userDto.setId(mood.getUser().getId());
        userDto.setUsername(mood.getUser().getUsername());
        userDto.setProfilePicture(mood.getUser().getProfilePicture());
        response.setUser(userDto);

        // 处理评论
        List<Comment> commentsCopy = new ArrayList<>(mood.getComments()); // 创建评论集合的副本
        List<MoodResponse.CommentDto> commentDtos = commentsCopy.stream()
                .map(this::getCommentDto)
                .collect(Collectors.toList());

        response.setComments(commentDtos);

        return response;
    }

    private MoodResponse.CommentDto getCommentDto(Comment comment) {
        MoodResponse.CommentDto commentDto = new MoodResponse.CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());

        // 设置评论用户信息
        MoodResponse.UserDto commentUserDto = new MoodResponse.UserDto();
        commentUserDto.setId(comment.getUser().getId());
        commentUserDto.setUsername(comment.getUser().getUsername());
        commentUserDto.setProfilePicture(comment.getUser().getProfilePicture());
        commentDto.setUser(commentUserDto);
        return commentDto;
    }
}
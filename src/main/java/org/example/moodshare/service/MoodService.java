package org.example.moodshare.service;

import org.example.moodshare.dto.CommentResponse;
import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.dto.MoodResponse;
import org.example.moodshare.model.Mood;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.MoodRepository;
import org.example.moodshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class  MoodService {

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MoodResponse createMood(MoodCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        Mood mood = new Mood();
        mood.setContent(request.getContent());
        mood.setEmoji(request.getEmoji());
        mood.setTags(request.getTags());
        mood.setUser(user);

        mood = moodRepository.save(mood);

        return convertToMoodResponse(mood, username);
    }


    @Transactional(readOnly = true)
    public List<MoodResponse> getAllMoods(String currentUsername) {
        List<Mood> moods = moodRepository.findAll();
        return moods.stream()
                .map(mood -> convertToMoodResponse(mood, currentUsername))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public MoodResponse getMoodById(Long id, String currentUsername) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));
        return convertToMoodResponse(mood, currentUsername);
    }


    @Transactional
    public boolean toggleLike(Long moodId, String username) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        Set<User> likedUsers = mood.getLikedBy();

        // 使用 contains 检查而不是在遍历中修改
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);  // 直接移除，不在遍历中操作
            return false; // 已取消点赞
        } else {
            likedUsers.add(user);  // 添加用户到点赞集合
            return true; // 已点赞
        }

        // 保存更改
        // moodRepository.save(mood); // 由于使用了@Transactional，这行可选
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

    private MoodResponse convertToMoodResponse(Mood mood, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        MoodResponse response = new MoodResponse();
        response.setId(mood.getId());
        response.setContent(mood.getContent());
        response.setEmoji(mood.getEmoji());
        response.setTags(mood.getTags());
        response.setCreatedAt(mood.getCreatedAt());
        response.setUsername(mood.getUser().getUsername());
        response.setLikeCount(mood.getLikeCount());

        // 检查当前用户是否点赞
        response.setLikedByCurrentUser(currentUser != null &&
                mood.getLikedBy().contains(currentUser));

        // 将评论集合转换为列表
        List<CommentResponse> commentResponses = mood.getComments().stream()
                .map(comment -> {
                    CommentResponse cr = new CommentResponse();
                    cr.setId(comment.getId());
                    cr.setContent(comment.getContent());
                    cr.setCreatedAt(comment.getCreatedAt());
                    cr.setUsername(comment.getUser().getUsername());
                    return cr;
                })
                .collect(Collectors.toList());

        response.setComments(commentResponses);

        return response;
    }
}
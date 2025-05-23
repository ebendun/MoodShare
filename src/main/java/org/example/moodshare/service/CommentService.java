package org.example.moodshare.service;

import org.example.moodshare.dto.CommentRequest;
import org.example.moodshare.dto.CommentResponse;
import org.example.moodshare.model.Comment;
import org.example.moodshare.model.Mood;
import org.example.moodshare.model.User;
import org.example.moodshare.repository.CommentRepository;
import org.example.moodshare.repository.MoodRepository;
import org.example.moodshare.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentService(CommentRepository commentRepository, MoodRepository moodRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.moodRepository = moodRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CommentResponse addComment(Long moodId, CommentRequest request, String username) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "心情不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setMood(mood);
        comment.setCreatedAt(LocalDateTime.now());

        // 直接保存评论，让JPA处理关系
        Comment savedComment = commentRepository.save(comment);

        // 将 Comment 转换为 CommentResponse
        return mapToCommentResponse(savedComment);
    }


    private CommentResponse mapToCommentResponse(Comment comment) {
        // 使用 ModelMapper 将 Comment 转换为 CommentResponse
        CommentResponse response = modelMapper.map(comment, CommentResponse.class);
        if (comment.getUser() != null) {
            response.setUsername(comment.getUser().getUsername());
            response.setUserId(comment.getUser().getId());
            response.setUserProfilePicture(comment.getUser().getProfilePicture());
        }
        return response;
    }


    public List<CommentResponse> getCommentsByMoodId(Long moodId) {
        // 直接通过查询获取评论列表
        List<Comment> comments = commentRepository.findByMoodIdOrderByCreatedAtDesc(moodId);

        // 转换为 DTO 并返回
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteComment(Long moodId, Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在"));

        if(!comment.getMood().getId().equals(moodId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评论与心情不匹配");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 检查是否是评论作者、心情发布者或管理员
        checkPermission(comment, user);

        // 直接删除评论
        commentRepository.delete(comment);
    }
    /**
     * 检查用户是否有权限删除评论
     */
    private void checkPermission(Comment comment, User user){
        String username = user.getUsername();
        boolean isCommentOwner = comment.getUser().getUsername().equals(username);
        boolean isMoodOwner = comment.getMood().getUser().getUsername().equals(username);
        boolean isAdmin = user.isAdmin();
        if (!isCommentOwner && !isMoodOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此评论");
        }

    }

    /**
     * 分页获取评论
     */
    public Page<CommentResponse> getCommentsByMoodId(Long moodId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByMoodId(moodId, pageable);
        return commentPage.map(this::mapToCommentResponse);
    }
}
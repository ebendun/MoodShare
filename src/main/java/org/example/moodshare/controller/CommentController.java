package org.example.moodshare.controller;

import org.example.moodshare.dto.CommentRequest;
import org.example.moodshare.dto.CommentResponse;
import org.example.moodshare.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/moods/{moodId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long moodId,
            @RequestBody CommentRequest request,
            Principal principal) {
        CommentResponse response = commentService.addComment(moodId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByMoodId(
            @PathVariable Long moodId) {
        List<CommentResponse> comments = commentService.getCommentsByMoodId(moodId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long moodId,
            @PathVariable Long commentId,
            Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
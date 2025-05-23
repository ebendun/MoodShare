package org.example.moodshare.controller;

import jakarta.validation.Valid;
import org.example.moodshare.dto.CommentRequest;
import org.example.moodshare.dto.CommentResponse;
import org.example.moodshare.service.CommentService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;


@RestController
@RequestMapping("/moods/{moodId}/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);


    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long moodId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        log.info("User {} is adding a comment to mood {}", username, moodId);
        CommentResponse response = commentService.addComment(moodId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getCommentsByMoodId(
            @PathVariable Long moodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.debug("Fetching Mood Id [{}] Comments", moodId);

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommentResponse> comments = commentService.getCommentsByMoodId(moodId, pageable);
        log.debug("Fetched {} comments for Mood Id [{}]", comments.getContent().size(), moodId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long moodId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("User {} is deleting comment {} from mood {}", username, commentId, moodId);
        commentService.deleteComment(moodId, commentId, userDetails.getUsername());
        log.info("Comment {} deleted successfully", commentId);
        return ResponseEntity.noContent().build();
    }
}
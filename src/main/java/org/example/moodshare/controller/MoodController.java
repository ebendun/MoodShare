package org.example.moodshare.controller;

import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.dto.MoodResponse;
import org.example.moodshare.model.Mood;
import org.example.moodshare.service.MoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/moods") // Changed from "/api/moods" to match frontend requests
public class MoodController {


    private final MoodService moodService;
    public MoodController(final MoodService moodService) {
        this.moodService = moodService;
    }

    @PostMapping
    public ResponseEntity<?> createMood(
            @RequestBody MoodCreateRequest request, 
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "创建心情需要登录"
            ));
        }
        MoodResponse response = moodService.createMood(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MoodResponse>> getAllMoods(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Mood.PrivacyLevel privacyLevel,
            @RequestParam(required = false) Mood.MoodType moodType,
            @RequestParam(required = false) String location) {
        // Handle both authenticated and unauthenticated requests
        String username = userDetails != null ? userDetails.getUsername() : null;
        List<MoodResponse> moods = moodService.getAllMoods(username, privacyLevel, moodType, location);
        return ResponseEntity.ok(moods);
    }
    
    @GetMapping("/feed")
    public ResponseEntity<List<MoodResponse>> getFeed(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MoodResponse> moods = moodService.getFeed(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(moods);
    }
    
    @GetMapping("/friends")
    public ResponseEntity<List<MoodResponse>> getFriendsMoods(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MoodResponse> moods = moodService.getFriendsMoods(userDetails.getUsername());
        return ResponseEntity.ok(moods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoodResponse> getMoodById(
            @PathVariable Long id, 
            @AuthenticationPrincipal UserDetails userDetails) {
        MoodResponse mood = moodService.getMoodById(id, userDetails.getUsername());
        return ResponseEntity.ok(mood);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MoodResponse>> getUserMoods(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MoodResponse> moods = moodService.getUserMoods(userId, userDetails.getUsername());
        return ResponseEntity.ok(moods);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @PathVariable Long id, 
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = moodService.toggleLike(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MoodResponse> updateMood(
            @PathVariable Long id,
            @RequestBody MoodCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MoodResponse mood = moodService.updateMood(id, request, userDetails.getUsername());
        return ResponseEntity.ok(mood);
    }
    
    @PutMapping("/{id}/privacy")
    public ResponseEntity<MoodResponse> updatePrivacy(
            @PathVariable Long id,
            @RequestParam Mood.PrivacyLevel privacyLevel,
            @AuthenticationPrincipal UserDetails userDetails) {
        MoodResponse mood = moodService.updatePrivacy(id, privacyLevel, userDetails.getUsername());
        return ResponseEntity.ok(mood);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<MoodResponse>> getNearbyMoods(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MoodResponse> moods = moodService.getNearbyMoods(latitude, longitude, radiusKm, userDetails.getUsername());
        return ResponseEntity.ok(moods);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMood(
            @PathVariable Long id, 
            @AuthenticationPrincipal UserDetails userDetails) {
        moodService.deleteMood(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
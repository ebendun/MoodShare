package org.example.moodshare.controller;

import org.example.moodshare.dto.MoodCreateRequest;
import org.example.moodshare.dto.MoodResponse;
import org.example.moodshare.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    @Autowired
    private MoodService moodService;

    @PostMapping
    public ResponseEntity<MoodResponse> createMood(@RequestBody MoodCreateRequest request, Principal principal) {
        MoodResponse response = moodService.createMood(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MoodResponse>> getAllMoods(Principal principal) {
        List<MoodResponse> moods = moodService.getAllMoods(principal.getName());
        return ResponseEntity.ok(moods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoodResponse> getMoodById(@PathVariable Long id, Principal principal) {
        MoodResponse mood = moodService.getMoodById(id, principal.getName());
        return ResponseEntity.ok(mood);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable Long id, Principal principal) {
        boolean liked = moodService.toggleLike(id, principal.getName());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMood(@PathVariable Long id, Principal principal) {
        moodService.deleteMood(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
package org.example.moodshare.repository;

import org.example.moodshare.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    @Query("SELECT m FROM Mood m LEFT JOIN FETCH m.comments LEFT JOIN FETCH m.likedBy")
    List<Mood> findAllWithCommentsAndLikes();
}
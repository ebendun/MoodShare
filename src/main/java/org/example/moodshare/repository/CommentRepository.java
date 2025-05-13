package org.example.moodshare.repository;

import org.example.moodshare.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMoodId(Long moodId);
    List<Comment> findByMoodIdOrderByCreatedAtDesc(Long moodId);
}
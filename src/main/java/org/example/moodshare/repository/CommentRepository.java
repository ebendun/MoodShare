package org.example.moodshare.repository;

import org.example.moodshare.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
//对评论的操作
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.mood.id = :moodId")
    List<Comment> findByMoodId(@Param("moodId") Long moodId);//根据心情ID查找评论

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.mood.id = :moodId ORDER BY c.createdAt DESC")
    List<Comment> findByMoodIdOrderByCreatedAtDesc(@Param("moodId") Long moodId);//根据心情ID查找评论并按创建时间降序排列

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.mood.id = :moodId")
    Page<Comment> findByMoodId(@Param("moodId") Long moodId, Pageable pageable);//根据心情ID查找评论并分页显示
}
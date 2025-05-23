package org.example.moodshare.repository;

import org.example.moodshare.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
//对评论的操作
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMoodId(Long moodId);//根据心情ID查找评论

    List<Comment> findByMoodIdOrderByCreatedAtDesc(Long moodId);//根据心情ID查找评论并按创建时间降序排列

    Page<Comment> findByMoodId(Long moodId, Pageable pageable);//根据心情ID查找评论并分页显示
}
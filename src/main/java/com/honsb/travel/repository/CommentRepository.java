package com.honsb.travel.repository;

import com.honsb.travel.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBoardId(Long boardId);
    List<Comment> findAllByUserLoginId(String loginId);

}

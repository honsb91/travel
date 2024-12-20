package com.honsb.travel.repository;

import com.honsb.travel.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    void deleteByUserLoginIdAndBoardId(String loginId, Long boardId);
    Boolean existsByUserLoginIdAndBoardId(String loginId, Long boardId);
    List<Like> findAllByUserLoginId(String loginId);
}

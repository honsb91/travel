package com.honsb.travel.repository;

import com.honsb.travel.domain.entity.User;
import com.honsb.travel.domain.enum_class.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByLoginId(String loginId);
    Page<User> findAllByNicknameContains(String nickname, PageRequest pageRequest);
    Boolean existsByLoginId(String loginId);
    Boolean existsByNickname(String nickname);
    Long countAllByUserRole(UserRole userRole);
}

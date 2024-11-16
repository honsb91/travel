package com.honsb.travel.domain.dto;

import com.honsb.travel.domain.entity.User;
import com.honsb.travel.domain.enum_class.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserJoinRequest {

    private String loginId;
    private String password;
    private String passwordCheck;
    private String nickname;

    public User toEntity(String encodedPassword){
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .nickname(nickname)
                .userRole(UserRole.BRONZE)
                .createdAt(LocalDateTime.now())
                .receivedLikeCnt(0)
                .build();
    }

}

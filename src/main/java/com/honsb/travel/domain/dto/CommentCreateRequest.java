package com.honsb.travel.domain.dto;

import com.honsb.travel.domain.entity.Board;
import com.honsb.travel.domain.entity.Comment;
import com.honsb.travel.domain.entity.User;
import lombok.Data;

@Data
public class CommentCreateRequest {

    private String body;

    public Comment toEntity(Board board, User user){
        return Comment.builder()
                .user(user)
                .board(board)
                .body(body)
                .build();
    }
}

package com.honsb.travel.service;

import com.honsb.travel.domain.dto.CommentCreateRequest;
import com.honsb.travel.domain.entity.Board;
import com.honsb.travel.domain.entity.Comment;
import com.honsb.travel.domain.entity.User;
import com.honsb.travel.domain.enum_class.UserRole;
import com.honsb.travel.repository.BoardRepository;
import com.honsb.travel.repository.CommentRepository;
import com.honsb.travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
// 댓글에 관련된 CRUD 로직처리
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public void writeComment(Long boardId, CommentCreateRequest req, String loginId){
        Board board = boardRepository.findById(boardId).get();
        User user = userRepository.findByLoginId(loginId).get();
        board.commentChange(board.getCommentCnt() + 1);
        commentRepository.save(req.toEntity(board, user));
    }

    public List<Comment> findAll(Long boardId){
        return commentRepository.findAllByBoardId(boardId);
    }

    @Transactional
    public Long editComment(Long commentId, String newBody, String loginId){
        Optional<Comment> optComment = commentRepository.findById(commentId);
        Optional<User> optUser = userRepository.findByLoginId(loginId);
        if (optComment.isEmpty() || optUser.isEmpty() || !optComment.get().getUser().equals(optUser.get())){
            return null;
        }

        Comment comment = optComment.get();
        comment.update(newBody);

        return comment.getBoard().getId();
    }

    public Long deleteComment(Long commentId, String loginId){
        Optional<Comment> optComment = commentRepository.findById(commentId);
        Optional<User> optUser = userRepository.findByLoginId(loginId);
        if (optComment.isEmpty() || optUser.isEmpty() ||
                (!optComment.get().getUser().equals(optUser.get()) && !optUser.get().getUserRole().equals(UserRole.ADMIN))){
            return null;
        }

        Board board = optComment.get().getBoard();
        board.commentChange(board.getCommentCnt() + 1);

        commentRepository.delete(optComment.get());
        return board.getId();
    }


}

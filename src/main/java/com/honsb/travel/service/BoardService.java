package com.honsb.travel.service;

import com.honsb.travel.domain.dto.BoardCntDto;
import com.honsb.travel.domain.dto.BoardCreateRequest;
import com.honsb.travel.domain.dto.BoardDto;
import com.honsb.travel.domain.entity.*;
import com.honsb.travel.domain.enum_class.BoardCategory;
import com.honsb.travel.domain.enum_class.UserRole;
import com.honsb.travel.repository.BoardRepository;
import com.honsb.travel.repository.CommentRepository;
import com.honsb.travel.repository.LikeRepository;
import com.honsb.travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final S3UploadService s3UploadService;

    public Page<Board> getBoardList(BoardCategory category, PageRequest pageRequest, String searchType, String keyword){
        if (searchType != null && keyword !=null){
            if (searchType.equals("title")){
                return boardRepository.findAllByCategoryAndTitleContainsAndUserUserRoleNot(category,keyword, UserRole.ADMIN,pageRequest);
            }else {
                return boardRepository.findAllByCategoryAndUserNicknameContainsAndUserUserRoleNot(category,keyword,UserRole.ADMIN,pageRequest);
            }
        }
        return boardRepository.findAllByCategoryAndUserUserRoleNot(category,UserRole.ADMIN,pageRequest);
    }

    public List<Board> getNotice(BoardCategory category){
        return boardRepository.findAllByCategoryAndUserUserRole(category,UserRole.ADMIN);
    }

    public BoardDto getBoard(Long boardId,String category){
        Optional<Board> optBoard = boardRepository.findById(boardId);

        // id에 해당하는 게시글이 없거나 카테고리가 일치하지 않으면 null return
        if(optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)){
            return null;
        }
        return BoardDto.of(optBoard.get());
    }

    @Transactional
    public Long writeBoard(BoardCreateRequest req, BoardCategory category, String loginId, Authentication auth) throws IOException{
        User loginUser = userRepository.findByLoginId(loginId).get();

        Board savedBoard = boardRepository.save(req.toEntity(category, loginUser));

        UploadImage uploadImage = uploadImageService.saveImage(req.getUploadImage(), savedBoard);
        if (uploadImage != null){
            savedBoard.setUploadImage(uploadImage);
        }

        if (category.equals(BoardCategory.GREETING)){
            loginUser.rankUp(UserRole.SILVER,auth);
        }

        return savedBoard.getId();
    }

    @Transactional
    public Long editBoard(Long boardId, String category, BoardDto dto) throws IOException{
        Optional<Board> optBoard = boardRepository.findById(boardId);

        // id에 해당하는 게시글이 없거나 카테고리가 일치하지 않으면 null return
        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)){
            return null;
        }

        Board board = optBoard.get();
        // 게시글에 이미지가 있었으면 삭제
        if(board.getUploadImage() != null){
            uploadImageService.deleteImage(board.getUploadImage());
            board.setUploadImage(null);
        }

        UploadImage uploadImage = uploadImageService.saveImage(dto.getNewImage(), board);
        if (uploadImage != null){
            board.setUploadImage(uploadImage);
        }
        board.update(dto);
        return board.getId();
    }

    public Long deleteBoard(Long boardId, String category) throws IOException{
        Optional<Board> optBoard = boardRepository.findById(boardId);

        // id에 해당하는 게시글이 없거나 카테고리가 일치하지 않으면 null return
        if (optBoard.isEmpty() || !optBoard.get().getCategory().toString().equalsIgnoreCase(category)){
            return null;
        }

        User boardUser = optBoard.get().getUser();
        boardUser.likeChange(boardUser.getReceivedLikeCnt() - optBoard.get().getLikeCnt());
        if (optBoard.get().getUser() !=null){
            uploadImageService.deleteImage(optBoard.get().getUploadImage());
        }
        boardRepository.deleteById(boardId);
        return boardId;
    }

    public String getCategory(Long boardId){
        Board board = boardRepository.findById(boardId).get();
        return board.getCategory().toString().toLowerCase();
    }

    public List<Board> findMyBoard(String category,String loginId){
        if (category.equals("board")){
            return boardRepository.findAllByUserLoginId(loginId);
        }else if (category.equals("like")){
            List<Like> likes = likeRepository.findAllByUserLoginId(loginId);
            List<Board> boards = new ArrayList<>();
            for (Like like : likes){
                boards.add(like.getBoard());
            }
            return boards;
        }else if (category.equals("comment")){
            List<Comment> comments = commentRepository.findAllByUserLoginId(loginId);
            List<Board> boards = new ArrayList<>();
            HashSet<Long> commentIds = new HashSet<>();

            for (Comment comment : comments){
                if (!commentIds.contains(comment.getBoard().getId())){
                    boards.add(comment.getBoard());
                    commentIds.add(comment.getBoard().getId());
                }
            }
            return boards;
        }
        return null;
    }

    public BoardCntDto getBoardCnt(){
        return BoardCntDto.builder()
                .totalBoardCnt(boardRepository.count())
                .totalNoticeCnt(boardRepository.countAllByUserUserRole(UserRole.ADMIN))
                .totalGreetingCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.GREETING,UserRole.ADMIN))
                .totalFreeCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.FREE,UserRole.ADMIN))
                .totalGoldCnt(boardRepository.countAllByCategoryAndUserUserRoleNot(BoardCategory.GOLD,UserRole.ADMIN))
                .build();
    }
}

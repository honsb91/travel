package com.honsb.travel.service;

import com.honsb.travel.domain.dto.BoardDto;
import com.honsb.travel.domain.entity.Board;
import com.honsb.travel.domain.enum_class.BoardCategory;
import com.honsb.travel.domain.enum_class.UserRole;
import com.honsb.travel.repository.BoardRepository;
import com.honsb.travel.repository.CommentRepository;
import com.honsb.travel.repository.LikeRepository;
import com.honsb.travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public Long writeBoard(BoardCreateRequest)
}

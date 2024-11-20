package com.honsb.travel.service;

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
}

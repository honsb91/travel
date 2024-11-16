package com.honsb.travel.service;

import com.honsb.travel.domain.dto.UserJoinRequest;
import com.honsb.travel.repository.CommentRepository;
import com.honsb.travel.repository.LikeRepository;
import com.honsb.travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder encoder;

    public BindingResult joinValid(UserJoinRequest req,BindingResult bindingResult){
        if(req.getLoginId().isEmpty()){
            bindingResult.addError(new FieldError("req","loginId","아이디가 비어있습니다."));
        }
    }

}

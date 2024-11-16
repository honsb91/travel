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
        }else if (req.getLoginId().length() > 10){
            bindingResult.addError(new FieldError("req","loginId","아이디가 10자가 넘습니다."));
        }else if (userRepository.existsByLoginId(req.getLoginId())){
            bindingResult.addError(new FieldError("req","loginId","아이디가 중복됩니다."));
        }

        if(req.getPassword().isEmpty()){
            bindingResult.addError(new FieldError("req","password","비밀번호가 비어있습니다."));
        }

        if(!req.getPassword().equals(req.getPasswordCheck())){
            bindingResult.addError(new FieldError("req","passwordCheck","비밀번호가 일치하지 않습니다."));
        }



    }

}

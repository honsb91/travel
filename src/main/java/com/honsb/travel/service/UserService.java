package com.honsb.travel.service;

import com.honsb.travel.domain.dto.UserDto;
import com.honsb.travel.domain.dto.UserJoinRequest;
import com.honsb.travel.domain.entity.User;
import com.honsb.travel.repository.CommentRepository;
import com.honsb.travel.repository.LikeRepository;
import com.honsb.travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.transaction.Transactional;

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

        if (req.getNickname().isEmpty()){
            bindingResult.addError(new FieldError("req","nickname","닉네임이 비어있습니다."));
        }else if (req.getNickname().length() > 10){
            bindingResult.addError(new FieldError("req","nickname","닉네임이 10자가 넘습니다."));
        }else if(userRepository.existsByNickname(req.getNickname())){
            bindingResult.addError(new FieldError("req","nickname","닉네임이 중복됩니다."));
        }

        return bindingResult;

    }

    public void join(UserJoinRequest req){
        userRepository.save(req.toEntity(encoder.encode(req.getPassword())));
    }

    public User myInfo(String loginId){
        return userRepository.findByLoginId(loginId).get();
    }

    public BindingResult editValid(UserDto dto,BindingResult bindingResult, String loginId){
        User loginUser = userRepository.findByLoginId(loginId).get();

        if(dto.getNowPassword().isEmpty()){
            bindingResult.addError(new FieldError("dto","nowPassword","현재 비밀번호가 비어있습니다."));
        }else if (!encoder.matches(dto.getNowPassword(), loginUser.getPassword())){
            bindingResult.addError(new FieldError("dto","nowPassword","현재 비밀번호가 틀렸습니다."));
        }

        if(!dto.getNewPassword().equals(dto.getNewPasswordCheck())){
            bindingResult.addError(new FieldError("dto","newPasswordCheck","비밀번호가 일치하지 않습니다."));
        }

        if (dto.getNickname().isEmpty()){
            bindingResult.addError(new FieldError("dto","nickname","닉네임이 비어있습니다."));
        }else if (dto.getNickname().length() > 10){
            bindingResult.addError(new FieldError("dto","nickname","닉네임이 10자가 넘습니다."));
        }else if (!dto.getNickname().equals(loginUser.getNickname()) && userRepository.existsByNickname(dto.getNickname())){
            bindingResult.addError(new FieldError("dto","nickname","닉네임이 중복됩니다."));
        }

        return bindingResult;
    }

    @Transactional
    public void edit(UserDto dto,String loginId){
        User loginUser = userRepository.findByLoginId(loginId).get();

        if (dto.getNewPassword().equals("")){
            loginUser.edit(loginUser.getPassword(), dto.getNickname());
        }else {
            loginUser.edit(encoder.encode(dto.getNewPassword()), dto.getNickname());
        }
    }


}

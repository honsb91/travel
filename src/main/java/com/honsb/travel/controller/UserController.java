package com.honsb.travel.controller;

import com.honsb.travel.domain.dto.UserJoinRequest;
import com.honsb.travel.domain.dto.UserLoginRequest;
import com.honsb.travel.service.BoardService;
import com.honsb.travel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BoardService boardService;

    @GetMapping("/join")
    public String joinPage(Model model){
        model.addAttribute("userJoinRequest",new UserJoinRequest());
        return "users/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute UserJoinRequest req, BindingResult bindingResult, Model model){

        // Validation
        if (userService.joinValid(req, bindingResult).hasErrors()){
            return "users/join";
        }

        userService.join(req);
        model.addAttribute("message","회원가입에 성공했습니다!\n 로그인 후 사용 가능합니다!");
        model.addAttribute("nextUrl","/users/login");
        return "printMessage";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request){

        // 로그인 성공 시 이전 페이지로 redirect 되게 하기 위해 세션에 저장
        String uri = request.getHeader("Referer");
        if (uri != null && uri.contains("/login") && !uri.contains("/join")){
            request.getSession().setAttribute("prevPage",uri);
        }

        model.addAttribute("userLoginRequest",new UserLoginRequest());
        return "users/login";
    }

    @GetMapping("/myPage/{category}")
    public String myPage(@PathVariable String category, Authentication auth, Model model){
        model.addAttribute("boards", boardService.find)
    }

}

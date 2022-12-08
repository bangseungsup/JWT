package com.hanghaeblog.hanghaeblog.Controller;

import com.hanghaeblog.hanghaeblog.dto.LoginRequestDto;
import com.hanghaeblog.hanghaeblog.dto.SignupRequestDto;
import com.hanghaeblog.hanghaeblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    // 회원가입 API
    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequestDto signupRequestDto){
        /*SignUpRequestDto
            {
                "username" : "request값"
                "password" : "request값"
             };
        */
        // 1. userService의 회원가입 메소드를 호출 , 파라미터로 클라이언트에게 전달 받은 data가 할당된 signupRequestDto를 넘겨준다.
        userService.signup(signupRequestDto);
        return "redirect:/api/user/login";
    }

    @PostMapping
    public String login(@RequestBody LoginRequestDto loginRequestDto){
        /*SignUpRequestDto
            {
                "username" : "request값"
                "password" : "request값"
             };
        */
        userService.login(loginRequestDto);
        return "success";
    }


}

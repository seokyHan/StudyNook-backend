package com.studyNook.member.controller;

import com.studyNook.member.dto.MemberInfoDto;
import com.studyNook.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        return memberService.userLogout(request);
    }

    @PostMapping("/reissue")
    public ResponseEntity<MemberInfoDto> reissue(@CookieValue("refreshToken") String cookieRefreshToken, HttpServletResponse response){
        return memberService.reissue(response, cookieRefreshToken);
    }

}

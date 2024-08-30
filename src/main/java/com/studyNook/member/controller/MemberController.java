package com.studyNook.member.controller;

import com.studyNook.member.dto.MemberInfoDto;
import com.studyNook.member.dto.MemberLoginDto;
import com.studyNook.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<MemberInfoDto> login(@Valid @RequestBody MemberLoginDto memberLoginDto) {
        //Todo 로그인 서비스 구현
        return null;
    }
}

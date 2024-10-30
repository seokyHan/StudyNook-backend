package com.studyNook.common.options.controller;

import com.studyNook.common.options.dto.SignupOptionDto;
import com.studyNook.common.options.service.SignupOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/options")
public class SignupOptionController {

    private final SignupOptionService service;

    @GetMapping
    public ResponseEntity<SignupOptionDto> getSignUpOptions() {
        return ResponseEntity.ok(service.findSignupOptions());
    }
}

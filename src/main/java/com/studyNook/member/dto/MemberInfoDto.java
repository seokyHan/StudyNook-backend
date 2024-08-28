package com.studyNook.member.dto;

import lombok.Builder;

public record MemberInfoDto(String accessToken,
                            String refreshToken,
                            Long id,
                            String userEmail,
                            String nickname,
                            String authorities) {

}

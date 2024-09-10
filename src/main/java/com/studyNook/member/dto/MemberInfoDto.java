package com.studyNook.member.dto;

import lombok.Builder;

public record MemberInfoDto(String accessToken,
                            String refreshToken,
                            Long id,
                            String email,
                            String nickname,
                            String authorities) {

    public static MemberInfoDto of(String accessToken, String refreshToken, Long id,
                                   String email, String nickname, String authorities) {
        return  new MemberInfoDto(accessToken, refreshToken, id, email, nickname, authorities);
    }

}

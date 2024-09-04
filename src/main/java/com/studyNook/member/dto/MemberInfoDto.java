package com.studyNook.member.dto;

import lombok.Builder;

public record MemberInfoDto(String accessToken,
                            String refreshToken,
                            Long id,
                            String userEmail,
                            String nickname,
                            String authorities) {

    public static MemberInfoDto of(String accessToken, String refreshToken, Long id,
                                   String userEmail, String nickname, String authorities) {
        return  new MemberInfoDto(accessToken, refreshToken, id, userEmail, nickname, authorities);
    }

}

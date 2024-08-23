package com.studyNook.global.common.exception.code;

import com.studyNook.global.common.exception.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.leftPad;

@Getter
@RequiredArgsConstructor
public enum AuthResponseCode implements ResponseCode {

    // 공통 코드 정의
    UNAUTHORIZED("1", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("2", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("3", HttpStatus.NOT_FOUND),
    GROUP_USER_NOT_FOUND("4", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_TOKEN_ERROR("5", HttpStatus.NOT_FOUND),
    SESSION_UNAUTHORIZED("6", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("7", HttpStatus.FORBIDDEN),
    USER_PROJECT_NOT_FOUND("8", HttpStatus.NOT_FOUND),
    USER_API_KEY_NOT_FOUND("9", HttpStatus.NOT_FOUND),
    NOT_EQUALS_LOGIN_IP("10", HttpStatus.UNAUTHORIZED),
    RSA_KEY_CREATE_ERROR("11", HttpStatus.INTERNAL_SERVER_ERROR),
    RSA_KEY_DECRYPT_ERROR("12", HttpStatus.BAD_REQUEST),
    AUTH_FAIL("13", HttpStatus.UNAUTHORIZED),
    ;

    private final String code;
    private final HttpStatus httpStatus;

    public String getCode() {
        return String.format("I-AUT-%s", leftPad(code, 4, "0"));
    }
}

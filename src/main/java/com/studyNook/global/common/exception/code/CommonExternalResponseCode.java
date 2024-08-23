package com.studyNook.global.common.exception.code;

import com.studyNook.global.common.exception.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum CommonExternalResponseCode implements ResponseCode {
    BODY_IS_EMPTY("1", INTERNAL_SERVER_ERROR),
    API_EXCHANGE_ERROR("2", HttpStatus.SERVICE_UNAVAILABLE)
    ;

    private final String code;
    private final HttpStatus httpStatus;

    public String getCode() {
        return String.format("E-COM-%s", leftPad(code, 4, "0"));
    }
}
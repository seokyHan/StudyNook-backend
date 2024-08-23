package com.studyNook.global.common.exception.code;


import com.studyNook.global.common.exception.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CommonResponseCode implements ResponseCode {
    // 공통 코드 정의
    SUCCESS("0", OK),
    REQUIRED_ERROR("2", BAD_REQUEST),
    CODE_NOT_FOUND("3", BAD_REQUEST),
    PARAMETER_TYPE_ERROR("4", BAD_REQUEST),
    PARAMETER_ERROR("5", BAD_REQUEST),
    INFO_NOT_FOUND("6", NOT_FOUND),
    ALREADY_IN_PROGRESS("7", BAD_REQUEST),
    API_NOT_FOUND("8", NOT_FOUND),
    BAD_REQUEST_ERROR("9", BAD_REQUEST),
    PARAMETER_ERROR_NO("10", BAD_REQUEST),
    PROJECT_ID_NOT_FOUND("11", BAD_REQUEST),
    ZONE_NOT_FOUND("12", BAD_REQUEST),
    FAILED_MAILING("13", INTERNAL_SERVER_ERROR),
    SERVICE_SQ_NOT_FOUND("14", NOT_FOUND),
    PRODUCT_CODE_NOT_FOUND("15", NOT_FOUND),
    UNKNOWN_ERROR("9999", INTERNAL_SERVER_ERROR)
    ;

    private final String code;
    private final HttpStatus httpStatus;

    public String getCode() {
        return String.format("I-COM-%s", leftPad(code, 4, "0"));
    }
}
package com.studyNook.global.common.exception.code;

import com.studyNook.global.common.exception.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.leftPad;

@Getter
@RequiredArgsConstructor
public enum FileResponseCode implements ResponseCode {
    FILE_TRANSFER_FAIL("1", HttpStatus.BAD_REQUEST),
    FILE_READ_ERROR("2", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_WRITE_ERROR("3", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_EXTENSION_ERROR("4", HttpStatus.BAD_REQUEST),
    FILE_EXCEED_MAX_SIZE("5", HttpStatus.BAD_REQUEST),
    FILE_NOT_EXISTS_TO_UPLOAD("6", HttpStatus.BAD_REQUEST),
    FILE_MIMETYPE_ERROR("7", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final HttpStatus httpStatus;

    public String getCode() {
        return String.format("I-FIL-%s", leftPad(code, 4, "0"));
    }
}
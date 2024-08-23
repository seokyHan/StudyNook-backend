package com.studyNook.global.common.exception;

import org.springframework.http.HttpStatus;

public interface ResponseCode {
    String getCode();
    HttpStatus getHttpStatus();
}

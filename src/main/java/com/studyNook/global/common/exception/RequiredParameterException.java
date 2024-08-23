package com.studyNook.global.common.exception;

import com.studyNook.global.common.exception.code.CommonResponseCode;
import lombok.Getter;

@Getter
public class RequiredParameterException extends RuntimeException {

    private final ResponseCode responseCode = CommonResponseCode.REQUIRED_ERROR;

    public RequiredParameterException(String requiredParameterName) {
        super(requiredParameterName);
    }

}

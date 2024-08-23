package com.studyNook.global.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.studyNook.global.common.exception.code.CommonResponseCode.SUCCESS;

@RequiredArgsConstructor
@Slf4j
@Component
public class Message {
    private final MessageSource messageSource;

    public String getOKResponse(String... values){
        return messageSource.getMessage(SUCCESS.getCode(), values, Locale.getDefault());
    }

    public String getResponseMessage(final ResponseCode responseCode, final String defaultMessage, final String... values) {
        String message = defaultMessage;

        try {
            log.info("responseCode.getCode() = {} ", responseCode.getCode());
            message = messageSource.getMessage(responseCode.getCode(), values, Locale.getDefault());
            log.info("message = {} ", message);
        } catch (NoSuchMessageException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return message;
    }

}

package com.studyNook.global.common.exception;


import com.studyNook.global.common.exception.code.CommonResponseCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class RestResponse {
    private String code;
    private String message;

    public RestResponse(){}

    public static ResponseEntity<RestResponse> toResponseEntity(ResponseCode responseCode, String message) {
        return ResponseEntity.status(responseCode.getHttpStatus()).body(RestResponse.builder()
                                                                                 .code(responseCode.getCode())
                                                                                 .message(message)
                                                                                 .build());
    }

    public static ResponseEntity<RestResponse> toResponseEntity(WebClientRetrieveException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(RestResponse.builder()
                                                                         .code(e.getCode())
                                                                         .message(e.getMessage())
                                                                         .build());
    }

    public static ResponseEntity<RestResponse> toOKResponseEntity(String message) {
        return RestResponse.toResponseEntity(CommonResponseCode.SUCCESS, message);
    }

    public RestResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
package com.jungle.chalnaServer.global.common.dto;

import com.jungle.chalnaServer.global.exception.GlobalErrorCode;
import org.springframework.http.HttpStatus;

public record CommonResponse<T>(String code, T data, String message) {
    public static CommonResponse<?> ok(Object data, String message){
        return new CommonResponse<>(String.valueOf(HttpStatus.OK.value()),data,message);
    }
    public static CommonResponse<?> from(HttpStatus httpStatus, Object data, String message){
        return new CommonResponse<>(String.valueOf(httpStatus.value()),data,message);
    }
    public static CommonResponse<?> from(String code,Object data, String message){
        return new CommonResponse<>(code,data,message);
    }

    public static CommonResponse<?> of(GlobalErrorCode errorCode){
        return new CommonResponse<>(errorCode.getCode(),null, errorCode.getMessage());
    }
}

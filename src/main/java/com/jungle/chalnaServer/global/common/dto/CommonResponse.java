package com.jungle.chalnaServer.global.common.dto;

import com.jungle.chalnaServer.global.exception.GlobalErrorCode;
import org.springframework.http.HttpStatus;

public record CommonResponse<T>(String code, T data, String message) {
    public static <T> CommonResponse<T> ok(T data){
        return new CommonResponse<>(String.valueOf(HttpStatus.OK.value()),data,"요청 처리에 성공했습니다.");
    }
    public static <T> CommonResponse<T> from(HttpStatus httpStatus, T data){
        return new CommonResponse<>(String.valueOf(httpStatus.value()),data,"요청 처리에 성공했습니다.");
    }
    public static <T> CommonResponse<T> from(String code,T data, String message){
        return new CommonResponse<>(code,data,message);
    }

    public static CommonResponse<?> ok(GlobalErrorCode errorCode){
        return new CommonResponse<>(errorCode.getCode(),null, errorCode.getMessage());
    }
}

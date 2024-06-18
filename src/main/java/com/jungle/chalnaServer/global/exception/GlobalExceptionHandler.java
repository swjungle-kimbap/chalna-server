package com.jungle.chalnaServer.global.exception;

import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Map<Class<? extends Exception>,GlobalErrorCode> exceptions;
    public GlobalExceptionHandler(){
        Map<Class<? extends Exception>, GlobalErrorCode> tempMap = new HashMap<>();
        for (GlobalErrorCode errorCode : GlobalErrorCode.values()) {
            errorCode.getExceptions()
                    .forEach((exception)->tempMap.put(exception,errorCode));
        }

        this.exceptions = Collections.unmodifiableMap(tempMap);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<CommonResponse<?>> customGlobalExceptionHandler(GlobalException e){
        if(exceptions.containsKey(e.getClass())){
            GlobalErrorCode errorCode = exceptions.get(e.getClass());
            return ResponseEntity.status(errorCode.getStatus()).body(CommonResponse.of(errorCode));
        }

        log.error("Unexpected error occurred",e);
        GlobalErrorCode errorCode = GlobalErrorCode.UNEXPECTED_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(CommonResponse.of(errorCode));
    }
}

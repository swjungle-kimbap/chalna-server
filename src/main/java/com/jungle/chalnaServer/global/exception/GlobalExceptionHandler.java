package com.jungle.chalnaServer.global.exception;

import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> customGlobalExceptionHandler(Exception e){
        if(e.getClass().isAssignableFrom(CustomException.class)){
            CustomException customException = (CustomException) e;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.from("400", "null", customException.getMessage()));
        }
        if(exceptions.containsKey(e.getClass())){
            GlobalErrorCode errorCode = exceptions.get(e.getClass());
            log.warn("exception resolved: {}",e.getClass().getName());
            return ResponseEntity.status(errorCode.getStatus()).body(CommonResponse.ok(errorCode));
        }

        log.error("Unexpected error occurred",e);
        GlobalErrorCode errorCode = GlobalErrorCode.UNEXPECTED_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(CommonResponse.ok(errorCode));
    }
}

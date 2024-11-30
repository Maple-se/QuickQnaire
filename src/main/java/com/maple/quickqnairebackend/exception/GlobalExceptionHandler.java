package com.maple.quickqnairebackend.exception;

/**
 * Created by zong chang on 2024/11/30 19:55
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleValidationExceptions(BindException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("字段验证失败: " + ex.getMessage());
    }
}

package com.resume.screening.auth.util;

import com.resume.screening.auth.exception.UserAlreadyExistException;
import com.resume.screening.auth.exception.UserNotFoundException;
import com.resume.screening.auth.payload.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@RestControllerAdvice
public class GlobalException {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AppResponse> handleUserNotFoundException(UserNotFoundException ex){

        AppResponse response = AppResponse.builder().message(ex.getMessage()).success(false).status(HttpStatus.NOT_FOUND).timeStamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<AppResponse> handleUserAlreadyExistsException(UserAlreadyExistException ex){

        AppResponse response = AppResponse.builder().message(ex.getMessage()).success(false).status(HttpStatus.IM_USED).timeStamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(response, HttpStatus.IM_USED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, Locale locale){
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = (fieldError != null)
                ? messageSource.getMessage(fieldError, locale)
                : "Validation failed";
        AppResponse response = AppResponse.builder()
                .message(message)
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

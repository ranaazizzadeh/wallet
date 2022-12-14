package com.example.wallet.controllers;

import com.example.wallet.exceptions.WalletException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
@EnableWebMvc
public class ControllerExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WalletException.class)
    public @ResponseBody ErrorMessage handleTodoException(WalletException ex,Locale locale){
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        errorMessage.setError(ex.getMessage());
        return errorMessage;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.toList());
        errorMessage.setError(errors.toString());
        return errorMessage;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public @ResponseBody ErrorMessage handleException(Exception ex){
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        errorMessage.setError(ex.getMessage());
        return errorMessage;
    }


    @Data
    class ErrorMessage{
        private  Long timestamp;
        private  Integer status;
        private  String error;
    }

}

package com.igorgorbunov3333.timer.backend.controller.advice;

import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(EntityDoesNotExist.class)
    public ResponseEntity<Response> handleException(EntityDoesNotExist e) {
        Response response = new Response(e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}

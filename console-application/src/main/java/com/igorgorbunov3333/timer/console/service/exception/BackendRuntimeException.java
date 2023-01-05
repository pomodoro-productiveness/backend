package com.igorgorbunov3333.timer.console.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackendRuntimeException extends RuntimeException {

    public BackendRuntimeException(String message) {
        super(message);
    }

}
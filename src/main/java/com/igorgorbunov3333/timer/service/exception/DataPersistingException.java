package com.igorgorbunov3333.timer.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataPersistingException extends RuntimeException {

    public DataPersistingException(String message) {
        super(message);
    }

}

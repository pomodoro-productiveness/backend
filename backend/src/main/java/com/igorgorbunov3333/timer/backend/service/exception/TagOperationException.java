package com.igorgorbunov3333.timer.backend.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagOperationException extends RuntimeException {

    public TagOperationException(String message) {
        super(message);
    }

}

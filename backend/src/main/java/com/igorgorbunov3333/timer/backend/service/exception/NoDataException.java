package com.igorgorbunov3333.timer.backend.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoDataException extends RuntimeException {

    public NoDataException(String message) {
        super(message);
    }

}

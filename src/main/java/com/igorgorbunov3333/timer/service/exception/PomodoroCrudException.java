package com.igorgorbunov3333.timer.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PomodoroCrudException extends RuntimeException {

    public PomodoroCrudException(String message) {
        super(message);
    }

}

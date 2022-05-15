package com.igorgorbunov3333.timer.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PomodoroException extends RuntimeException {

    public PomodoroException(String message) {
        super(message);
    }

}

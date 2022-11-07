package com.igorgorbunov3333.timer.backend.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PomodoroEngineException extends RuntimeException {

    public PomodoroEngineException(String message) {
        super(message);
    }

}
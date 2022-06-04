package com.igorgorbunov3333.timer.service.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PomodoroSynchronizationException extends RuntimeException {

    public PomodoroSynchronizationException(String message) {
        super(message);
    }

}

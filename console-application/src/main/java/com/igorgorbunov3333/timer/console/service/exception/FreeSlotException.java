package com.igorgorbunov3333.timer.console.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FreeSlotException extends RuntimeException {

    public FreeSlotException(String message) {
        super(message);
    }

}
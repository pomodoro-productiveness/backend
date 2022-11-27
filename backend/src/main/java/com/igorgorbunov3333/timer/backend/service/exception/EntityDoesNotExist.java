package com.igorgorbunov3333.timer.backend.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityDoesNotExist extends RuntimeException {

    public EntityDoesNotExist(String message) {
        super(message);
    }

}

package com.igorgorbunov3333.timer.backend.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityAlreadyExists extends RuntimeException {

    public EntityAlreadyExists(String message) {
        super(message);
    }

}

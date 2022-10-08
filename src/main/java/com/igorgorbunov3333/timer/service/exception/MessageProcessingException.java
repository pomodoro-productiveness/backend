package com.igorgorbunov3333.timer.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageProcessingException extends RuntimeException{

    public MessageProcessingException(String message) {
        super(message);
    }

}

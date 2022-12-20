package com.igorgorbunov3333.timer.backend.model.entity.enums;

import com.igorgorbunov3333.timer.backend.service.exception.BackendRuntimeException;

public enum MessagePeriod {

    DAY("day"),
    WEEK("week"),
    MONTH("month");

    MessagePeriod(String value) {
        this.value = value;
    }

    private final String value;

    public static MessagePeriod from(String input) {
        for (MessagePeriod period : values()) {
            if (period.value.equals(input.toLowerCase())) {
                return period;
            }
        }

        throw new BackendRuntimeException(String.format("No enum value for [%s]", input));
    }

}

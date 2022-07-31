package com.igorgorbunov3333.timer.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberToItemBuilder {

    public static <T> Map<Integer, T> build(Iterable<T> items) {
        int count = 0;

        Map<Integer, T> result = new LinkedHashMap<>();
        for (T item : items) {
            result.put(++count, item);
        }

        return result;
    }

}

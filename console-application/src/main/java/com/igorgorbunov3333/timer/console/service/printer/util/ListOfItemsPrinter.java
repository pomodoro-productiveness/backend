package com.igorgorbunov3333.timer.console.service.printer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListOfItemsPrinter {

    public static <T, R> void print(Map<Integer, T> numberByItems, Function<T, R> function) {
        int longestNumber = String.valueOf(numberByItems.size()).length();
        for (Map.Entry<Integer, T> entry : numberByItems.entrySet()) {
            StringBuilder spacesAfterNumber = new StringBuilder(StringUtils.SPACE);
            int additionalSpacesAmount = longestNumber - String.valueOf(entry.getKey()).length();

            spacesAfterNumber.append(StringUtils.SPACE.repeat(Math.max(0, additionalSpacesAmount)));

            String itemRow = entry.getKey() + PrintUtil.DOT + spacesAfterNumber + function.apply(entry.getValue());

            SimplePrinter.print(itemRow);
        }
    }

}

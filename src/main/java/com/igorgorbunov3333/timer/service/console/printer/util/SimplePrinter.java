package com.igorgorbunov3333.timer.service.console.printer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimplePrinter {

    private static final String YES_NO_QUESTION = "Yes (y), No";
    private static final String INCORRECT_NUMBER = "Number [%d] is incorrect";

    public static void print(String message) {
        System.out.println(message);
    }

    public static void printWithoutCarriageOffset(String message) {
        System.out.print(message);
    }

    public static void printParagraph() {
        System.out.print(StringUtils.LF);
    }

    public static void printYesNoQuestion() {
        print(YES_NO_QUESTION);
    }

    public static void printIncorrectNumber(int number) {
        print(String.format(INCORRECT_NUMBER, number));
    }

}

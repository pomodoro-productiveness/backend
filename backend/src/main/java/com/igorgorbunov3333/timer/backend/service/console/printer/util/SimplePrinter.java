package com.igorgorbunov3333.timer.backend.service.console.printer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimplePrinter {

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
        print(PrintUtil.YES_NO_QUESTION);
    }

    public static void printTryAgainMessage() {
        print(PrintUtil.TRY_AGAIN_MESSAGE);
    }

    public static void printIncorrectNumber(int number) {
        print(String.format(PrintUtil.INCORRECT_NUMBER, number));
    }

}

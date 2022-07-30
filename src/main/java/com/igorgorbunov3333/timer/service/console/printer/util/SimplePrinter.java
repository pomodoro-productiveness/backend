package com.igorgorbunov3333.timer.service.console.printer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimplePrinter {

    public static void print(String message) {
        System.out.println(message);
    }

    public static void printWithoutCarriageOffset(String message) {
        System.out.print(message);
    }

}

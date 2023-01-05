package com.igorgorbunov3333.timer.console.service.command.line.provider;

import java.util.Scanner;

public interface BaseLineProvider {

    Scanner sc = new Scanner(System.in);

    default String provideLine() {
        return sc.nextLine();
    }

}

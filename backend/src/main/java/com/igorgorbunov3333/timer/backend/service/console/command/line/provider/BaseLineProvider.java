package com.igorgorbunov3333.timer.backend.service.console.command.line.provider;

import java.util.Scanner;

public interface BaseLineProvider { //TODO: think about to extract it to separate bean

    Scanner sc = new Scanner(System.in);

    default String provideLine() {
        return sc.nextLine();
    }

}

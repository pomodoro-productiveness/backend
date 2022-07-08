package com.igorgorbunov3333.timer.service.console.command.line.provider;

import java.util.Scanner;

public abstract class AbstractLineProvider { //TODO: think about to extract it to separate bean

    private static final Scanner sc = new Scanner(System.in);

    public String provideLine() {
        return sc.nextLine();
    }

}

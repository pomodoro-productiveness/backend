package com.igorgorbunov3333.timer.service.console.command;

public interface CommandProcessor {

    void process();

    //TODO: add enum to store commands
    String command();

}

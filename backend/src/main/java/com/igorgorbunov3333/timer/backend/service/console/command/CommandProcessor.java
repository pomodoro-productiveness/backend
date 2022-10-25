package com.igorgorbunov3333.timer.backend.service.console.command;

public interface CommandProcessor {

    void process();

    //TODO: add enum to store commands
    String command();

}

package com.igorgorbunov3333.timer.model;

import org.springframework.stereotype.Component;

@Component
public class PomodoroState {

    private int seconds;
    private boolean currentlyRunning;

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isCurrentlyRunning() {
        return currentlyRunning;
    }

    public void isRunning(boolean currentlyRunning) {
        this.currentlyRunning = currentlyRunning;
    }
}

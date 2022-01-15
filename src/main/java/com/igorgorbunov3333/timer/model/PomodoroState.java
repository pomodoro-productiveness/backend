package com.igorgorbunov3333.timer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class PomodoroState {

    private int seconds;
    private boolean running;

}

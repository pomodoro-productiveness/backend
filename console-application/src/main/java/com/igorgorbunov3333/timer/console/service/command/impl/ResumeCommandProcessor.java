package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngineComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResumeCommandProcessor implements CommandProcessor {

    private final PomodoroEngineComponent pomodoroEngineComponent;

    @Override
    public void process() {
        pomodoroEngineComponent.resumePomodoro();
        pomodoroEngineComponent.printThreeSecondsOfPomodoroExecution();
    }

    @Override
    public String command() {
        return "resume";
    }

}

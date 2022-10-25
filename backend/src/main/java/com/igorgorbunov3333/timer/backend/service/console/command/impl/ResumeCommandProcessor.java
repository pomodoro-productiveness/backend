package com.igorgorbunov3333.timer.backend.service.console.command.impl;

import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResumeCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;

    @Override
    public void process() {
        pomodoroEngineService.resumePomodoro();
        pomodoroEngineService.printThreeSecondsOfPomodoroExecution();
    }

    @Override
    public String command() {
        return "resume";
    }

}

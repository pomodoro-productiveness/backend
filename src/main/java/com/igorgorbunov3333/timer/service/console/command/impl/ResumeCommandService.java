package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResumeCommandService implements CommandService {

    private final PomodoroEngineService pomodoroEngineService;

    @Override
    public void process() {
        pomodoroEngineService.resumePomodoro();
    }

    @Override
    public String command() {
        return "resume";
    }

}

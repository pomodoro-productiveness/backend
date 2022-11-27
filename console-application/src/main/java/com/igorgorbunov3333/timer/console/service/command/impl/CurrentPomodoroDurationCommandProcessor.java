package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrentPomodoroDurationCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;

    @Override
    public void process() {
        String pomodoroCurrentDuration = pomodoroEngineService.getPomodoroCurrentDuration();
        SimplePrinter.print(pomodoroCurrentDuration);
    }

    @Override
    public String command() {
        return "3";
    }

}

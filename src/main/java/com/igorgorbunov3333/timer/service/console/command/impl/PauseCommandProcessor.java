package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PauseCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;

    @Override
    public void process() {
        pomodoroEngineService.pausePomodoro();
        SimplePrinter.print(String.format("Pomodoro paused at %s", pomodoroEngineService.getPomodoroCurrentDuration()));
    }

    @Override
    public String command() {
        return "pause";
    }

}

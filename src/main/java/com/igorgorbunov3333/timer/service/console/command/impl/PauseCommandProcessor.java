package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PauseCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public void process() {
        long durationInSeconds = pomodoroEngine.getPomodoroCurrentDuration();

        if (durationInSeconds <= pomodoroProperties.getDuration()) {
            SimplePrinter.print(String.format("To pause a pomodoro, its duration must be greater than the specified minimum: [%d] seconds)",
                    pomodoroProperties.getDuration()));

            return;
        }

        pomodoroEngineService.pausePomodoro();
        SimplePrinter.print(String.format("Pomodoro paused at %s", pomodoroEngineService.getPomodoroCurrentDuration()));
    }

    @Override
    public String command() {
        return "pause";
    }

}

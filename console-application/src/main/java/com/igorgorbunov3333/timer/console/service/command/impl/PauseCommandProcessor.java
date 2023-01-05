package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngineComponent;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PauseCommandProcessor implements CommandProcessor {

    private final PomodoroEngineComponent pomodoroEngineComponent;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public void process() {
        long durationInSeconds = pomodoroEngine.getPomodoroCurrentDuration();

        if (durationInSeconds <= pomodoroProperties.getDuration()) {
            SimplePrinter.print(String.format("To pause a pomodoro, its duration must be greater than the specified minimum: [%d] seconds",
                    pomodoroProperties.getDuration()));

            return;
        }

        pomodoroEngineComponent.pausePomodoro();
        SimplePrinter.print(String.format("Pomodoro paused at %s", pomodoroEngineComponent.getPomodoroCurrentDuration()));
    }

    @Override
    public String command() {
        return "pause";
    }

}

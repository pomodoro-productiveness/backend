package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngineComponent;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrentPomodoroDurationCommandProcessor implements CommandProcessor {

    private final PomodoroEngineComponent pomodoroEngineComponent;

    @Override
    public void process() {
        String pomodoroCurrentDuration = pomodoroEngineComponent.getPomodoroCurrentDuration();
        SimplePrinter.print(pomodoroCurrentDuration);
    }

    @Override
    public String command() {
        return "3";
    }

}

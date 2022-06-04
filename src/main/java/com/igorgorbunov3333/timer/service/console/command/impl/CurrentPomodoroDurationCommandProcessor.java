package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrentPomodoroDurationCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;
    private final PrinterService printerService;

    @Override
    public void process() {
        String pomodoroCurrentDuration = pomodoroEngineService.getPomodoroCurrentDuration();
        printerService.print(pomodoroCurrentDuration);
    }

    @Override
    public String command() {
        return "3";
    }

}

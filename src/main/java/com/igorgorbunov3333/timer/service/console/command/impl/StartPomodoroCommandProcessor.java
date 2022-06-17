package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StartPomodoroCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;
    private final PrinterService printerService;

    @Override
    public void process() {
        try {
            pomodoroEngineService.startPomodoro();
        } catch (PomodoroEngineException e) {
            printerService.print(e.getMessage());
            return;
        }
        printerService.print("Pomodoro has started");
        pomodoroEngineService.printThreeSecondsOfPomodoroExecution();
    }

    @Override
    public String command() {
        return "1";
    }

}

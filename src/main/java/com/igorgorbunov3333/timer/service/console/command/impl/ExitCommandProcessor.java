package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExitCommandProcessor implements CommandProcessor {

    private final PrinterService printerService;
    private final PomodoroEngine pomodoroEngine;

    @Override
    @SneakyThrows
    public void process() {
        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            printerService.print("Unable to close the application because there is a running pomodoro");
            return;
        }

        if (pomodoroEngine.isPomodoroPaused()) {
            printerService.print("Unable to close the application because there is a paused pomodoro");
            return;
        }

        printerService.print("Closing the application");
        System.exit(0);
    }

    @Override
    public String command() {
        return "e";
    }

}

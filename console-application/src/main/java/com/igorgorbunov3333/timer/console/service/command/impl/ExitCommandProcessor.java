package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExitCommandProcessor implements CommandProcessor {

    private final PomodoroEngine pomodoroEngine;

    @Override
    @SneakyThrows
    public void process() {
        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            SimplePrinter.print("Unable to close the application because there is a running pomodoro");
            return;
        }

        if (pomodoroEngine.isPomodoroPaused()) {
            SimplePrinter.print("Unable to close the application because there is a paused pomodoro");
            return;
        }

        SimplePrinter.print("Closing the application");
        System.exit(0);
    }

    @Override
    public String command() {
        return "e";
    }

}

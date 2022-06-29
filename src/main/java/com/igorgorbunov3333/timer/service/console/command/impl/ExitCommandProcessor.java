package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.synchronization.priority.local.LocalPrioritySynchronizer;
import com.igorgorbunov3333.timer.service.synchronization.toggler.LocalPrioritySynchronizationToggler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExitCommandProcessor implements CommandProcessor {

    private final LocalPrioritySynchronizationToggler localPrioritySynchronizationToggler;
    private final PrinterService printerService;
    private final LocalPrioritySynchronizer localPrioritySynchronizer;
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

        if (!localPrioritySynchronizationToggler.isNeedToSynchronize()) {
            printerService.print("Closing the application");
            localPrioritySynchronizer.synchronize();
            System.exit(0);
        } else {
            printerService.print("Unable to close the application now due to not finished synchronization. "
                    + "Please try again later");
        }
    }

    @Override
    public String command() {
        return "e";
    }

}

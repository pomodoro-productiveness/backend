package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroPauseTimer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StartPomodoroCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;
    private final PrinterService printerService;
    private final PomodoroPauseTimer pomodoroPauseTimer;
    private final PomodoroProperties pomodoroProperties;

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

        String currentCommand = CurrentCommandStorage.currentCommand;

        String[] commandParts = currentCommand.split(StringUtils.SPACE);

        if (commandParts.length < 2) {
            return;
        }

        Integer durationInMinutes = null;
        try {
            durationInMinutes = Integer.valueOf(commandParts[1]);
        } catch (NumberFormatException e) {
            String className = this.getClass().getSimpleName();
            log.error(String.format("Error while parsing command in %s", className), e);
        }

        if (durationInMinutes != null && isValidDuration(durationInMinutes)) {
            pomodoroPauseTimer.conduct(durationInMinutes * 60);
        }
    }

    private boolean isValidDuration(Integer minutes) {
        if (minutes >= pomodoroProperties.getDuration()
            || minutes >= pomodoroProperties.getAutomaticShutdownDuration()) {
            printerService.print("Duration must be less then pomodoro standard duration an less then automatic shutdown duration");

            return false;
        }

        return true;
    }

    @Override
    public String command() {
        return "1";
    }

}

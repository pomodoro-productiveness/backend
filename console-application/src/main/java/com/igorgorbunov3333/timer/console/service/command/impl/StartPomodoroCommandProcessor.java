package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.backend.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroPauseTimer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StartPomodoroCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;
    private final PomodoroPauseTimer pomodoroPauseTimer;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public void process() {
        try {
            pomodoroEngineService.startPomodoro();

            Integer minutesToPauseDuration = getMinutesToPauseDuration();
            if (minutesToPauseDuration != null && isValidDuration(minutesToPauseDuration)) {
                pomodoroPauseTimer.conduct(minutesToPauseDuration * 60);
            }
        } catch (PomodoroEngineException e) {
            SimplePrinter.print(e.getMessage());
            return;
        }
        SimplePrinter.print("Pomodoro started:");
        pomodoroEngineService.printThreeSecondsOfPomodoroExecution();
    }

    private Integer getMinutesToPauseDuration() {
        String currentCommand = CurrentCommandStorage.currentCommand;

        String[] commandParts = currentCommand.split(StringUtils.SPACE);

        if (commandParts.length < 2) {
            return null;
        }

        Integer timerToPauseDurationInMinutes = null;
        try {
            timerToPauseDurationInMinutes = Integer.valueOf(commandParts[1]);
        } catch (NumberFormatException e) {
            String className = this.getClass().getSimpleName();
            log.error(String.format("Error while parsing command in %s", className), e);
        }

        return timerToPauseDurationInMinutes;
    }

    private boolean isValidDuration(Integer minutes) {
        if (minutes >= pomodoroProperties.getDuration()
                || minutes >= pomodoroProperties.getAutomaticShutdownDuration()) {
            SimplePrinter.print("Duration must be less then pomodoro standard duration an less then automatic shutdown duration");

            return false;
        }

        return true;
    }

    @Override
    public String command() {
        return "1";
    }

}

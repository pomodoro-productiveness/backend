package com.igorgorbunov3333.timer.service.pomodoro.engine.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.service.commandline.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultPomodoroEngineService implements PomodoroEngineService {

    private static final String MESSAGE_POMODORO_PAUSED = "Pomodoro is paused now: ";
    private static final String MESSAGE_POMODORO_NOT_STARTED = "Pomodoro did not started!";

    private final PomodoroService pomodoroService;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;
    private final PrinterService printerService;

    @Override
    public void startPomodoro() {
        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            String message = "Pomodoro is running now: " + getPomodoroCurrentDurationInString();
            throw new PomodoroEngineException(message);
        }
        if (pomodoroEngine.isPomodoroPaused()) {
            String message = MESSAGE_POMODORO_PAUSED + getPomodoroCurrentDurationInString();
            throw new PomodoroEngineException(message);
        }
        pomodoroEngine.startPomodoro();
    }

    @Override
    public PomodoroDto stopPomodoro() {
        if (!pomodoroEngine.isPomodoroCurrentlyRunning() && !pomodoroEngine.isPomodoroPaused()) {
            throw new PomodoroEngineException(MESSAGE_POMODORO_NOT_STARTED);
        }
        int duration = pomodoroEngine.stopPomodoro();
        long pomodoroMinimumLifetime = pomodoroProperties.getMinimumLifetime();
        if (pomodoroMinimumLifetime == 0) {
            System.out.println("Pomodoro lifetime didn't set. Please configure");
        }
        if (duration <= pomodoroMinimumLifetime) {
            String message = "Pomodoro lifetime is less then [" + pomodoroMinimumLifetime + "] seconds";
            throw new PomodoroEngineException(message);
        }
        return pomodoroService.saveByDuration(duration);
    }

    @Override
    public String getPomodoroCurrentDuration() {
        if (!pomodoroEngine.isPomodoroCurrentlyRunning() && !pomodoroEngine.isPomodoroPaused()) {
            return MESSAGE_POMODORO_NOT_STARTED;
        }
        return getPomodoroCurrentDurationInString();
    }

    @Override
    public String getPomodoroCurrentDurationInString() {
        int seconds = pomodoroEngine.getPomodoroCurrentDuration();
        return SecondsFormatter.formatInMinutes(seconds);
    }

    @Override
    public void pausePomodoro() {
        pomodoroEngine.pausePomodoro();
    }

    @Override
    public void resumePomodoro() {
        pomodoroEngine.resumePomodoro();
    }

    @Override
    @SneakyThrows
    public void printFirstThreeFirstPomodoroSecondsDuration() {
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1100);
            String formattedTime = getPomodoroCurrentDurationInString();
            printerService.print(formattedTime);
        }
    }

}

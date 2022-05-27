package com.igorgorbunov3333.timer.service.pomodoro.engine.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroPausesStorage;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPomodoroEngineService implements PomodoroEngineService {

    private static final String MESSAGE_POMODORO_PAUSED = "Pomodoro is paused now: ";
    private static final String MESSAGE_POMODORO_NOT_STARTED = "Pomodoro did not started!";

    private final PomodoroService pomodoroService;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;
    private final PrinterService printerService;
    private final PomodoroPausesStorage pomodoroPausesStorage;

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
            pomodoroPausesStorage.evict();
            throw new PomodoroEngineException(message);
        }
        List<Pair<ZonedDateTime, ZonedDateTime>> pomodoroPausePairs = pomodoroPausesStorage.getPauses();
        List<PomodoroPauseDto> pomodoroPauses = pomodoroPausePairs.stream()
                .map(pair -> new PomodoroPauseDto(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
        pomodoroPausesStorage.evict();
        return pomodoroService.saveByDurationWithPauses(duration, pomodoroPauses);
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

package com.igorgorbunov3333.timer.backend.service.pomodoro.engine.impl;

import com.igorgorbunov3333.timer.backend.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroPausesStorage;
import com.igorgorbunov3333.timer.backend.service.pomodoro.saver.PomodoroSaver;
import com.igorgorbunov3333.timer.backend.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultPomodoroEngineService implements PomodoroEngineService {

    private static final String MESSAGE_POMODORO_PAUSED = "Pomodoro is paused now: ";
    private static final String MESSAGE_POMODORO_NOT_STARTED = "Pomodoro did not started!";

    private final PomodoroSaver pomodoroSaver;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;
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
//        if (!pomodoroEngine.isPomodoroCurrentlyRunning() && !pomodoroEngine.isPomodoroPaused()) {
//            throw new PomodoroEngineException(MESSAGE_POMODORO_NOT_STARTED);
//        }
//        int duration = pomodoroEngine.stopPomodoro();
//        long pomodoroMinimumLifetime = pomodoroProperties.getMinimumLifetime();
//        if (pomodoroMinimumLifetime == 0) {
//            SimplePrinter.print("Pomodoro lifetime didn't set. Please configure");
//        }
//        if (duration <= pomodoroMinimumLifetime) {
//            String message = "Pomodoro lifetime is less then [" + pomodoroMinimumLifetime + "] seconds";
//            pomodoroPausesStorage.evict();
//            throw new PomodoroEngineException(message);
//        }
//        List<Pair<ZonedDateTime, ZonedDateTime>> pomodoroPausePairs = pomodoroPausesStorage.getPauses();
//        List<PomodoroPauseDto> pomodoroPauses = pomodoroPausePairs.stream()
//                .map(pair -> new PomodoroPauseDto(pair.getFirst(), pair.getSecond()))
//                .collect(Collectors.toList());
//        pomodoroPausesStorage.evict();
//        return pomodoroSaver.save(duration, pomodoroPauses);
        return null;
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
    public void printThreeSecondsOfPomodoroExecution() {
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1100);
            String formattedTime = getPomodoroCurrentDurationInString();
            SimplePrinter.print(formattedTime);
        }
    }

}

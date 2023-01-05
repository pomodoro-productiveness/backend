package com.igorgorbunov3333.timer.console.service.pomodoro.engine;

import com.igorgorbunov3333.timer.console.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroSaveRequestDto;
import com.igorgorbunov3333.timer.console.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PomodoroEngineComponent {

    private static final String MESSAGE_POMODORO_PAUSED = "Pomodoro is paused now: ";
    private static final String MESSAGE_POMODORO_NOT_STARTED = "Pomodoro did not started!";

    private final PomodoroComponent pomodoroComponent;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;
    private final PomodoroPausesStorage pomodoroPausesStorage;

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

    public PomodoroDto stopPomodoro() {
        if (!pomodoroEngine.isPomodoroCurrentlyRunning() && !pomodoroEngine.isPomodoroPaused()) {
            throw new PomodoroEngineException(MESSAGE_POMODORO_NOT_STARTED);
        }
        int duration = pomodoroEngine.stopPomodoro();
        long pomodoroMinimumLifetime = pomodoroProperties.getMinimumLifetime();
        if (pomodoroMinimumLifetime == 0) {
            SimplePrinter.print("Pomodoro lifetime didn't set. Please configure");
        }
        if (duration <= pomodoroMinimumLifetime) {
            String message = "Pomodoro lifetime is less then [" + pomodoroMinimumLifetime + "] seconds";
            pomodoroPausesStorage.evict();
            throw new PomodoroEngineException(message);
        }
        List<Pair<ZonedDateTime, ZonedDateTime>> pomodoroPausePairs = pomodoroPausesStorage.getPauses();
        List<PomodoroPauseDto> pomodoroPauses = pomodoroPausePairs.stream()
                .map(pair -> new PomodoroPauseDto(pair.getKey(), pair.getValue()))
                .collect(Collectors.toList());
        pomodoroPausesStorage.evict();

        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime endTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .atZone(currentZoneId);
        ZonedDateTime startTime = endTime.minusSeconds(duration);

        PomodoroSaveRequestDto saveRequest = new PomodoroSaveRequestDto(
                startTime,
                endTime,
                pomodoroPauses,
                0L
        );

        return pomodoroComponent.save(saveRequest);
    }

    public String getPomodoroCurrentDuration() {
        if (!pomodoroEngine.isPomodoroCurrentlyRunning() && !pomodoroEngine.isPomodoroPaused()) {
            return MESSAGE_POMODORO_NOT_STARTED;
        }
        return getPomodoroCurrentDurationInString();
    }

    public String getPomodoroCurrentDurationInString() {
        int seconds = pomodoroEngine.getPomodoroCurrentDuration();
        return SecondsFormatter.formatInMinutes(seconds);
    }

    public void pausePomodoro() {
        pomodoroEngine.pausePomodoro();
    }

    public void resumePomodoro() {
        pomodoroEngine.resumePomodoro();
    }

    @SneakyThrows
    public void printThreeSecondsOfPomodoroExecution() {
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1100);
            String formattedTime = getPomodoroCurrentDurationInString();
            SimplePrinter.print(formattedTime);
        }
    }

}

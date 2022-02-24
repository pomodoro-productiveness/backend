package com.igorgorbunov3333.timer.service.pomodoro.engine.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.engine.PomodoroActionInfoDto;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultPomodoroEngineService implements PomodoroEngineService {

    private static final String MESSAGE_POMODORO_PAUSED = "Pomodoro is paused now: ";
    private static final String MESSAGE_POMODORO_NOT_STARTED = "Pomodoro did not started!";

    private final PomodoroService pomodoroService;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public PomodoroActionInfoDto startPomodoro() {
        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            String message = "Pomodoro is running now: " + getPomodoroCurrentDurationInString();
            return new PomodoroActionInfoDto(false, message, null);
        }
        if (pomodoroEngine.isPomodoroPaused()) {
            String message = MESSAGE_POMODORO_PAUSED + getPomodoroCurrentDurationInString();
            return new PomodoroActionInfoDto(false, message, null);
        }
        pomodoroEngine.startPomodoro();
        return new PomodoroActionInfoDto(true, "", null);
    }

    @Override
    public PomodoroActionInfoDto stopPomodoro() {
        if (!pomodoroEngine.isPomodoroCurrentlyRunning() && !pomodoroEngine.isPomodoroPaused()) {
            return new PomodoroActionInfoDto(false, MESSAGE_POMODORO_NOT_STARTED, null);
        }
        int duration = pomodoroEngine.stopPomodoro();
        long pomodoroMinimumLifetime = pomodoroProperties.getMinimumLifetime();
        if (pomodoroMinimumLifetime == 0) {
            System.out.println("Pomodoro lifetime didn't set. Please configure");
        }
        if (duration <= pomodoroMinimumLifetime) {
            return new PomodoroActionInfoDto(false, "Pomodoro lifetime is less then [" + pomodoroMinimumLifetime + "] seconds", null);
        }
        PomodoroDto savedPomodoro = pomodoroService.saveByDuration(duration);
        if (savedPomodoro != null) {
            return new PomodoroActionInfoDto(true, "", savedPomodoro);
        }
        return new PomodoroActionInfoDto(false, "Pomodoro didn't save", null);
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

}

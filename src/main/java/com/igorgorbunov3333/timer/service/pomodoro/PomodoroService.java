package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PomodoroService {

    void starPomodoro();

    PomodoroDto stopAndSavePomodoro();

    int getPomodoroCurrentDuration();

    long getPomodorosInDay();

    List<PomodoroDto> getPomodorosInDayExtended();

    Map<LocalDate, List<PomodoroDto>> getMonthlyPomodoros();

    void removePomodoro(Long id);

    Long removeLatest();

    PomodoroDto save();

    boolean isNotActive();

    boolean isActive();

    boolean isPaused();

    void pause();

    void resume();
}

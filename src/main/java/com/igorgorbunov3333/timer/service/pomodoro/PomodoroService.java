package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.PomodoroDtoV2;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PomodoroService {

    void starPomodoro();

    PomodoroDtoV2 stopAndSavePomodoro();

    int getPomodoroCurrentDuration();

    long getPomodorosInDay();

    List<PomodoroDtoV2> getPomodorosInDayExtended();

    Map<LocalDate, List<PomodoroDtoV2>> getMonthlyPomodoros();

    void removePomodoro(Long id);

    PomodoroDtoV2 save();

    boolean isNotActive();

}

package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PomodoroService {

    void starPomodoro();

    void stopPomodoro();

    int getPomodoroTime();

    long getPomodorosInDay();

    List<Pomodoro> getPomodorosInDayExtended();

    Map<LocalDate, List<Pomodoro>> getPomodorosInMonthExtended();

    void removePomodoro(Long id);

    void save(Pomodoro pomodoroToSave);

}

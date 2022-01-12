package com.igorgorbunov3333.timer.service;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import org.springframework.scheduling.annotation.Async;

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
}

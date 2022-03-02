package com.igorgorbunov3333.timer.service.commandline;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PrinterService {

    void print(String message);

    void printFeaturesList();

    void printSavedAndDailyPomodorosAfterStoppingPomodoro(PomodoroDto savedPomodoro, List<PomodoroDto> dailyPomodoros);

    void printPomodorosWithIds(List<PomodoroDto> pomodoros);

    void printPomodorosWithIds(List<PomodoroDto> pomodoros, boolean withId);

    void printLocalDatePomodoros(Map<LocalDate, List<PomodoroDto>> datesToPomadoros);

    void printDayOfWeekToPomodoros(Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros);

}

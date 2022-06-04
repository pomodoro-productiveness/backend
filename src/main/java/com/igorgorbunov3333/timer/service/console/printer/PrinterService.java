package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

//TODO: use separate class with static method to print lines
public interface PrinterService {

    void print(String message);

    void printParagraph();

    void printFeaturesList();

    void printSavedAndDailyPomodorosAfterStoppingPomodoro(PomodoroDto savedPomodoro, List<PomodoroDto> dailyPomodoros);

    void printPomodorosWithIds(List<PomodoroDto> pomodoros);

    void printPomodorosWithIds(List<PomodoroDto> pomodoros, boolean withId);

    void printLocalDatePomodoros(Map<LocalDate, List<PomodoroDto>> datesToPomadoros);

    void printDayOfWeekToPomodoros(Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros);

}
package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

//TODO: use separate class with static method to print lines
public interface PrinterService {

    void print(String message);

    void printWithoutCarriageOffset(String message);

    void printParagraph();

    void printFeaturesList();

    void printSavedAndDailyPomodoroAfterStoppingPomodoro(PomodoroDto savedPomodoro, List<PomodoroDto> dailyPomodoros);

    void printPomodorosWithIdsAndTags(List<PomodoroDto> pomodoros);

    void printDayOfWeekToPomodoros(Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros);

    void printPomodoro(PomodoroDto pomodoro, boolean withIdAndTag, int number);

    void printYesNoQuestion();
}

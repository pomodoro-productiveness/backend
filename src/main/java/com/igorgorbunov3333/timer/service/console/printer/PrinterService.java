package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

//TODO: use separate class with static method to print lines
public interface PrinterService {

    void printFeaturesList();

    void printPomodoroWithIdsAndTags(List<PomodoroDto> pomodoro);

    void printDayOfWeekToPomodoro(Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoro);

}

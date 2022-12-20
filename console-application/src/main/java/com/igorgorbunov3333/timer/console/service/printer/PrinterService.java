package com.igorgorbunov3333.timer.console.service.printer;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;

import java.util.List;
import java.util.Map;

public interface PrinterService {

    void printFeaturesList();

    void printPomodoroWithIdsAndTags(List<PomodoroDto> pomodoro);

    void printDayOfWeekToPomodoro(Map<String, List<PomodoroDto>> weeklyPomodoro);

}

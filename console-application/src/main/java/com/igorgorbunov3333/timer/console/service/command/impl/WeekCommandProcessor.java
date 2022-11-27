package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.backend.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.WeeklyPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WeekCommandProcessor implements CommandProcessor {

    private static final String DAY_OFF = "DAY_OFF";
    private static final String SEPARATOR = "----------------------";

    private final WeeklyPomodoroProvider currentWeekLocalPomodoroProvider;
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;

    @Override
    @Transactional(readOnly = true)
    public void process() {
        WeeklyPomodoroDto weeklyPomodoroDto = currentWeekLocalPomodoroProvider.provideCurrentWeekPomodoro();

        Map<String, List<PomodoroDto>> weeklyPomodoro = mapToDayOfWeekWithDayOffToPomodoro(weeklyPomodoroDto);

        if (weeklyPomodoroDto.getPomodoro().isEmpty()) {
            SimplePrinter.print("No weekly pomodoro");
        }

        printerService.printDayOfWeekToPomodoro(weeklyPomodoro);

        List<PomodoroDto> pomodoro = weeklyPomodoro.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        standardReportPrinter.print(weeklyPomodoroDto.getPeriod(), pomodoro);
        tagDurationReportPrinter.print(pomodoro);
    }

    @Override
    public String command() {
        return "week";
    }

    private Map<String, List<PomodoroDto>> mapToDayOfWeekWithDayOffToPomodoro(WeeklyPomodoroDto weeklyPomodoroDto) {
        Map<String, List<PomodoroDto>> result = new LinkedHashMap<>();
        for (DailyPomodoroDto dailyPomodoro : weeklyPomodoroDto.getDailyPomodoro()) {
            String row = dailyPomodoro.getDayOfWeek().name();

            if (dailyPomodoro.isDayOff()) {
                row += SEPARATOR + DAY_OFF;
            }

            result.put(row, dailyPomodoro.getPomodoro());
        }

        return result;
    }

}

package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.provider.WeeklyPomodoroProvider;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import com.igorgorbunov3333.timer.console.service.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WeekCommandProcessor implements CommandProcessor {

    private static final String DAY_OFF = "DAY_OFF";
    private static final String SEPARATOR = "----------------------";

    private final WeeklyPomodoroProvider weeklyPomodoroProvider;
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;

    @Override
    public void process() {
        WeeklyPomodoroDto weeklyPomodoroDto = weeklyPomodoroProvider.provideCurrentWeekPomodoro();

        Map<String, List<PomodoroDto>> weeklyPomodoro = mapToDayOfWeekWithDayOffToPomodoro(weeklyPomodoroDto);

        if (weeklyPomodoroDto.getPomodoro().isEmpty()) {
            SimplePrinter.print("No weekly pomodoro");
        }

        printerService.printDayOfWeekToPomodoro(weeklyPomodoro);

        standardReportPrinter.print(weeklyPomodoroDto.getPeriod());
        tagDurationReportPrinter.print(weeklyPomodoroDto.getPeriod());
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

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentWeekPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WeekCommandProcessor implements CommandProcessor, CurrentWeekDaysProvidable {

    private final CurrentWeekPomodoroProvider currentWeekLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    @Getter
    private final CurrentTimeService currentTimeService;
    private final TagDurationReportPrinter tagDurationReportPrinter;

    @Override
    @Transactional(readOnly = true)
    public void process() {
        Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoro = currentWeekLocalPomodoroProvider.provideCurrentWeekPomodorosByDays();
        if (weeklyPomodoro.isEmpty()) {
            printerService.print("No weekly pomodoro");
        }
        printerService.printDayOfWeekToPomodoro(weeklyPomodoro);

        List<PomodoroDto> pomodoro = weeklyPomodoro.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<DayOfWeek> currentWeekDays = provideDaysOfCurrentWeek();

        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        LocalDate startPeriod = currentDay.minusDays(currentWeekDays.size());

        PeriodDto period = new PeriodDto(startPeriod.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period, pomodoro);
        tagDurationReportPrinter.print(pomodoro);
    }

    @Override
    public String command() {
        return "week";
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentMonthPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonthCommandProcessor implements CommandProcessor {

    private final CurrentMonthPomodoroProvider currentMonthLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final CurrentTimeService currentTimeService;
    private final TagDurationReportPrinter tagDurationReportPrinter;

    @Override
    @Transactional(readOnly = true)
    public void process() {
        List<PomodoroDto> monthlyPomodoro = currentMonthLocalPomodoroProvider.provide(null);
        Map<LocalDate, List<PomodoroDto>> datesToPomadoros = monthlyPomodoro.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().toLocalDate()));
        Map<LocalDate, List<PomodoroDto>> sortedPomodoros = new TreeMap<>(datesToPomadoros);
        if (sortedPomodoros.isEmpty()) {
            printerService.print("No monthly pomodoros");
            return;
        }
        printerService.printLocalDatePomodoros(sortedPomodoros);

        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        LocalDate startPeriod = currentDay.withDayOfMonth(1);

        PeriodDto period = new PeriodDto(startPeriod.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        List<PomodoroDto> pomodoro = sortedPomodoros.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        standardReportPrinter.print(period, pomodoro);
        tagDurationReportPrinter.print(pomodoro);
    }

    @Override
    public String command() {
        return "6";
    }

}

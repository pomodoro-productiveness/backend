package com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.month.MonthSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.MonthlyPomodoroReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CurrentMonthSessionProcessor implements MonthSessionProcessor {

    private final MonthlyPomodoroReportPrinter monthlyPomodoroReportPrinter;
    private final CurrentTimeService currentTimeService;

    @Override
    public void process(List<PomodoroDto> pomodoro) {
        PeriodDto currentMonthPeriod = getCurrentMonthPeriod();

        List<PomodoroDto> currentMonthPomodoro = pomodoro.stream()
                .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(currentMonthPeriod.getStart())
                        && !p.getStartTime().toLocalDateTime().isAfter(currentMonthPeriod.getEnd()))
                .collect(Collectors.toList());

        Map<LocalDate, List<PomodoroDto>> datesToPomodoro = currentMonthPomodoro.stream()
                .collect(Collectors.groupingBy(p -> p.getStartTime().toLocalDate()));
        Map<LocalDate, List<PomodoroDto>> sortedPomodoro = new TreeMap<>(datesToPomodoro);
        if (sortedPomodoro.isEmpty()) {
            SimplePrinter.print("No monthly pomodoro");
            return;
        }

        List<PomodoroDto> monthlyPomodoro = sortedPomodoro.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        monthlyPomodoroReportPrinter.print(currentMonthPeriod, monthlyPomodoro);

        SimplePrinter.printParagraph();
    }

    @Override
    public String action() {
        return "1";
    }

    private PeriodDto getCurrentMonthPeriod() {
        LocalDateTime nowTime = currentTimeService.getCurrentDateTime();

        YearMonth currentMonth = YearMonth.from(nowTime);

        return new PeriodDto(
                currentMonth.atDay(1).atStartOfDay(),
                nowTime.toLocalDate().atTime(LocalTime.MAX));
    }

}

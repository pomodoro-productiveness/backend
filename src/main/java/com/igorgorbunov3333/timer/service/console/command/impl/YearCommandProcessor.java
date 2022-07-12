package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.DefaultPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class YearCommandProcessor implements CommandProcessor {

    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;
    private final DefaultPomodoroProvider pomodoroProvider;
    private final CurrentTimeService currentTimeService;

    @Override
    public void process() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        ZonedDateTime start = today.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        List<PomodoroDto> yearlyPomodoro = pomodoroProvider.provide(start, end, null);

        standardReportPrinter.print(new PeriodDto(start.toLocalDateTime(), end.toLocalDateTime()), yearlyPomodoro);
        tagDurationReportPrinter.print(yearlyPomodoro);
    }

    @Override
    public String command() {
        return "year";
    }
}

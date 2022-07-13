package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.period.PomodoroMonthlyDivider;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.DefaultPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@AllArgsConstructor
public class YearCommandProcessor implements CommandProcessor {

    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;
    private final DefaultPomodoroProvider pomodoroProvider;
    private final CurrentTimeService currentTimeService;
    private final PomodoroMonthlyDivider divider;
    private final PrinterService printerService;

    @Override
    public void process() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        ZonedDateTime start = today.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        List<PomodoroDto> yearlyPomodoro = pomodoroProvider.provide(start, end, null);

        Map<PeriodDto, List<PomodoroDto>> monthlyPomodoro = divider.divide(yearlyPomodoro);

        int counter = 0;
        for (Map.Entry<PeriodDto, List<PomodoroDto>> entry: monthlyPomodoro.entrySet()) {
            Month month = Month.from(entry.getKey().getStart());

            printerService.printParagraph();
            printerService.print(++counter + DefaultPrinterService.DOT + StringUtils.SPACE + month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase());
            standardReportPrinter.print(entry.getKey(), entry.getValue());
            tagDurationReportPrinter.print(entry.getValue());
        }

        printerService.printParagraph();
        printerService.print("YEAR REPORT");
        standardReportPrinter.print(new PeriodDto(start.toLocalDateTime(), end.toLocalDateTime()), yearlyPomodoro);
        tagDurationReportPrinter.print(yearlyPomodoro);
    }

    @Override
    public String command() {
        return "year";
    }
}

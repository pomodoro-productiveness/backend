package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.period.PomodoroByMonthsDivider;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentYearPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
public class YearCommandProcessor implements CommandProcessor {

    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;
    private final PomodoroByMonthsDivider pomodoroByMonthsDivider;
    private final CurrentYearPomodoroProvider currentYearPomodoroProvider;
    private final CurrentTimeService currentTimeService;

    @Override
    public void process() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        ZonedDateTime start = today.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        List<PomodoroDto> yearlyPomodoro = currentYearPomodoroProvider.provide(null);

        Map<PeriodDto, List<PomodoroDto>> monthlyPomodoro = pomodoroByMonthsDivider.divide(yearlyPomodoro);

        int counter = 0;
        for (Map.Entry<PeriodDto, List<PomodoroDto>> entry: monthlyPomodoro.entrySet()) {
            Month month = Month.from(entry.getKey().getStart());

            SimplePrinter.printParagraph();
            SimplePrinter.print(++counter + PrintUtil.DOT + StringUtils.SPACE + month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase());
            standardReportPrinter.print(entry.getKey(), entry.getValue());
            tagDurationReportPrinter.print(entry.getValue());
        }

        SimplePrinter.printParagraph();
        SimplePrinter.print("YEAR REPORT");
        standardReportPrinter.print(new PeriodDto(start.toLocalDateTime(), end.toLocalDateTime()), yearlyPomodoro);
        tagDurationReportPrinter.print(yearlyPomodoro);
    }

    @Override
    public String command() {
        return "year";
    }
}

package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.YearlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.TagDurationReportPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.YearlyPomodoroProvider;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@AllArgsConstructor
public class YearCommandProcessor implements CommandProcessor {

    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;
    private final YearlyPomodoroProvider yearlyPomodoroProvider;

    @Override
    public void process() {
        YearlyPomodoroDto yearlyPomodoro = yearlyPomodoroProvider.provideCurrentYearPomodoro(null);

        int counter = 0;
        boolean first = true;
        for (MonthlyPomodoroDto monthlyPomodoro : yearlyPomodoro.getMonthlyPomodoro()) {
            Month month = Month.from(monthlyPomodoro.getPeriod().getStart());

            if (!first) {
                SimplePrinter.printParagraph();
            } else {
                first = false;
            }

            SimplePrinter.print(++counter + PrintUtil.DOT + StringUtils.SPACE +  month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase());
            standardReportPrinter.print(monthlyPomodoro.getPeriod(), monthlyPomodoro.getPomodoro());
            tagDurationReportPrinter.print(monthlyPomodoro.getPomodoro());
        }

        SimplePrinter.printParagraph();
        SimplePrinter.print("YEAR REPORT");
        standardReportPrinter.print(yearlyPomodoro.getPeriod(), yearlyPomodoro.getPomodoro());
        tagDurationReportPrinter.print(yearlyPomodoro.getPomodoro());
    }

    @Override
    public String command() {
        return "year";
    }

}

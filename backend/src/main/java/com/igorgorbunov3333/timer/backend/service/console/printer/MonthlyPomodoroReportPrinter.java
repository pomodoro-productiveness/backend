package com.igorgorbunov3333.timer.backend.service.console.printer;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MonthlyPomodoroReportPrinter {

    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;

    public void print(MonthlyPomodoroDto monthlyPomodoroDto) {
        List<WeeklyPomodoroDto> weeks = monthlyPomodoroDto.getWeeklyPomodoro();

        int counter = 0;

        for (WeeklyPomodoroDto weeklyPomodoroDto : weeks) {
            SimplePrinter.printParagraph();

            PeriodDto weekPeriod = weeklyPomodoroDto.getPeriod();

            SimplePrinter.print(++counter + PrintUtil.DOT + " WEEK ("
                    + weekPeriod.getStart().toLocalDate() + StringUtils.SPACE + "-" + StringUtils.SPACE
                    + weekPeriod.getEnd().toLocalDate() + ")");

            standardReportPrinter.print(weekPeriod, weeklyPomodoroDto.getPomodoro());
            tagDurationReportPrinter.print(weeklyPomodoroDto.getPomodoro());
        }

        SimplePrinter.printParagraph();
        SimplePrinter.print("MONTH REPORT");

        standardReportPrinter.print(monthlyPomodoroDto.getPeriod(), monthlyPomodoroDto.getPomodoro());
        tagDurationReportPrinter.print(monthlyPomodoroDto.getPomodoro());
    }

}

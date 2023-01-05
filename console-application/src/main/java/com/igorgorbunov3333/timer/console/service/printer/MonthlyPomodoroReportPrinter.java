package com.igorgorbunov3333.timer.console.service.printer;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
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

            standardReportPrinter.print(weekPeriod);
            tagDurationReportPrinter.print(
                    weekPeriod.getStart().toLocalDate(),
                    weekPeriod.getEnd().toLocalDate()
            );
        }

        SimplePrinter.printParagraph();
        SimplePrinter.print("MONTH REPORT");

        PeriodDto period = monthlyPomodoroDto.getPeriod();

        standardReportPrinter.print(period);
        tagDurationReportPrinter.print(
                period.getStart().toLocalDate(),
                period.getEnd().toLocalDate()
        );
    }

}

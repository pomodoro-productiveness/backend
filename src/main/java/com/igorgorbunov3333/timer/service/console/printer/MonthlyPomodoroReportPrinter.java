package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.period.PomodoroByWeekDivider;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class MonthlyPomodoroReportPrinter {

    private final StandardReportPrinter standardReportPrinter;
    private final TagDurationReportPrinter tagDurationReportPrinter;
    private final PomodoroByWeekDivider weekDivider;

    public void print(PeriodDto monthPeriod, List<PomodoroDto> pomodoro) {
        Map<PeriodDto, List<PomodoroDto>> weeks = weekDivider.divide(monthPeriod, pomodoro);

        int counter = 0;
        for (Map.Entry<PeriodDto, List<PomodoroDto>> entry : weeks.entrySet()) {
            SimplePrinter.printParagraph();
            SimplePrinter.print(++counter + PrintUtil.DOT + " WEEK ("
                    + entry.getKey().getStart().toLocalDate() + StringUtils.SPACE + "-" + StringUtils.SPACE
                    + entry.getKey().getEnd().toLocalDate() + ")");

            standardReportPrinter.print(entry.getKey(), entry.getValue());
            tagDurationReportPrinter.print(entry.getValue());
        }

        SimplePrinter.printParagraph();
        SimplePrinter.print("MONTH REPORT");

        standardReportPrinter.print(monthPeriod, pomodoro);
        tagDurationReportPrinter.print(pomodoro);
    }

}

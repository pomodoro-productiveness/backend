package com.igorgorbunov3333.timer.console.service.message.pomodoro.report.impl;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.console.service.message.pomodoro.report.MessageProvider;
import com.igorgorbunov3333.timer.console.service.period.WeekPeriodHelper;
import com.igorgorbunov3333.timer.console.service.pomodoro.report.PomodoroStandardReportComponent;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@AllArgsConstructor
public class WeekPomodoroStandardReportMessageProvider implements MessageProvider {

    private final WeekPeriodHelper weekPeriodHelper;
    private final PomodoroStandardReportComponent pomodoroStandardReportComponent;
    private final CurrentTimeComponent currentTimeComponent;

    public String provide(LocalDate reportDate) {
        if (isStartOfWeek(reportDate.plusDays(1L))) {
            return buildMessageForPreviousWeek();
        }

        LocalDate start = provideStartDayOfWeek();
        LocalDate end = currentTimeComponent.getCurrentDateTime()
                .toLocalDate();

        return buildReportMessage(start, end);
    }

    private String buildMessageForPreviousWeek() {
        PeriodDto previousWeekPeriod = weekPeriodHelper.providePreviousWeekPeriod();

        return buildReportMessage(
                previousWeekPeriod.getStart().toLocalDate(),
                previousWeekPeriod.getEnd().toLocalDate()
        );
    }

    private String buildReportMessage(LocalDate start, LocalDate end) {
        PomodoroStandardReportDto report = pomodoroStandardReportComponent.getReport(start, end);

        String header = "Report for: " + start + "  -  " + end + "\n";

        return buildReportMessage(report, header);
    }

    private boolean isStartOfWeek(LocalDate today) {
        return DayOfWeek.MONDAY.equals(today.getDayOfWeek());
    }

    private LocalDate provideStartDayOfWeek() {
        LocalDate currentDay = currentTimeComponent.getCurrentDateTime().toLocalDate();

        return currentDay.with(DayOfWeek.MONDAY);
    }

}

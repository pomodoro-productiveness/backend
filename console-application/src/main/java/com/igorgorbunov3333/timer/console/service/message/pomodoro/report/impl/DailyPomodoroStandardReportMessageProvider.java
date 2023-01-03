package com.igorgorbunov3333.timer.console.service.message.pomodoro.report.impl;

import com.igorgorbunov3333.timer.console.rest.dto.DayOffDto;
import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.console.service.dayoff.DayOffComponent;
import com.igorgorbunov3333.timer.console.service.message.pomodoro.report.MessageProvider;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.report.PomodoroStandardReportComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class DailyPomodoroStandardReportMessageProvider implements MessageProvider {

    private final PomodoroStandardReportComponent pomodoroStandardReportComponent;
    private final PomodoroComponent pomodoroComponent;
    private final DayOffComponent dayOffComponent;

    public String provide(LocalDate reportDate) {
        List<PomodoroDto> dailyPomodoro = pomodoroComponent.getPomodoro(reportDate, reportDate, null);
        List<DayOffDto> dayOffs = dayOffComponent.getDayOffs();

        boolean dayOff = dayOffs.stream()
                .map(DayOffDto::getDay)
                .anyMatch(dayOffDate -> dayOffDate.equals(reportDate));

        DailyPomodoroDto dailyPomodoroDto = new DailyPomodoroDto(
                dailyPomodoro,
                dayOff,
                reportDate.getDayOfWeek(),
                reportDate
        );

        PeriodDto period = dailyPomodoroDto.calculatePeriod();
        PomodoroStandardReportDto report = pomodoroStandardReportComponent.getReport(
                period.getStart().toLocalDate(),
                period.getEnd().toLocalDate()
        );

        String header = "Report for " + reportDate + "\n";

        return buildReportMessage(report, header);
    }

}

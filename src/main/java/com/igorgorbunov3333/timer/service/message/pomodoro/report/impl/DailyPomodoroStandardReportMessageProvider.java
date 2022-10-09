package com.igorgorbunov3333.timer.service.message.pomodoro.report.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.service.message.pomodoro.report.MessageProvider;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class DailyPomodoroStandardReportMessageProvider implements MessageProvider {

    private final PomodoroStandardReporter pomodoroStandardReporter;
    private final DailyPomodoroProvider dailyPomodoroProvider;

    public String provide(LocalDate reportDate) {
        DailyPomodoroDto dailyPomodoroDto = dailyPomodoroProvider.provide(reportDate);
        PomodoroStandardReportDto report = pomodoroStandardReporter.report(dailyPomodoroDto.calculatePeriod(), dailyPomodoroDto.getPomodoro());

        String header = "Report for " + reportDate + "\n";

        return buildReportMessage(report, header);
    }

}

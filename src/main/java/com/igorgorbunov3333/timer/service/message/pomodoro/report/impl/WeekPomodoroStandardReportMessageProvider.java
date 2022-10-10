package com.igorgorbunov3333.timer.service.message.pomodoro.report.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.service.exception.MessageProcessingException;
import com.igorgorbunov3333.timer.service.message.pomodoro.report.MessageProvider;
import com.igorgorbunov3333.timer.service.period.WeekPeriodHelper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class WeekPomodoroStandardReportMessageProvider implements MessageProvider {

    private final WeekPeriodHelper weekPeriodHelper;
    private final PomodoroStandardReporter pomodoroStandardReporter;
    private final WeeklyPomodoroProvider weeklyPomodoroProvider;

    public String provide(LocalDate reportDate) {
        if (isStartOfWeek(reportDate.plusDays(1L))) {
            return buildMessageForPreviousWeek();
        }

        WeeklyPomodoroDto weeklyPomodoroDto = weeklyPomodoroProvider.provideCurrentWeekPomodoro();

        return buildReportMessage(weeklyPomodoroDto);
    }

    private String buildMessageForPreviousWeek() {
        PeriodDto previousWeekPeriod = weekPeriodHelper.providePreviousWeekPeriod();
        List<WeeklyPomodoroDto> weeklyPomodoroDtoList = weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(previousWeekPeriod);

        if (CollectionUtils.isEmpty(weeklyPomodoroDtoList)) {
            throw new MessageProcessingException("No WeeklyPomodoroDto for period " + previousWeekPeriod);
        }

        return buildReportMessage(weeklyPomodoroDtoList.get(0));
    }

    private String buildReportMessage(WeeklyPomodoroDto weeklyPomodoroDto) {
        PomodoroStandardReportDto report = pomodoroStandardReporter.report(weeklyPomodoroDto.getPeriod(), weeklyPomodoroDto.getPomodoro());

        PeriodDto period = weeklyPomodoroDto.getPeriod();

        String header = "Report for period: " + period.getStart().toLocalDate() + "  -  " + period.getEnd().toLocalDate() + "\n";

        return buildReportMessage(report, header);
    }

    private boolean isStartOfWeek(LocalDate today) {
        return DayOfWeek.MONDAY.equals(today.getDayOfWeek());
    }

}

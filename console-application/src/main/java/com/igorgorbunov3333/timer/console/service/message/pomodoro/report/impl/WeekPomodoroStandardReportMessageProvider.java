package com.igorgorbunov3333.timer.console.service.message.pomodoro.report.impl;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.console.service.message.pomodoro.report.MessageProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        PeriodDto periodWithoutToday = new PeriodDto(
                weeklyPomodoroDto.getPeriod().getStart(),
                weeklyPomodoroDto.getPeriod().getEnd().minusDays(1L)
        );

        return buildReportMessage(weeklyPomodoroDto, periodWithoutToday);
    }

    private String buildMessageForPreviousWeek() {
        PeriodDto previousWeekPeriod = weekPeriodHelper.providePreviousWeekPeriod();
        List<WeeklyPomodoroDto> weeklyPomodoroDtoList = weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(previousWeekPeriod);

        if (CollectionUtils.isEmpty(weeklyPomodoroDtoList)) {
            throw new MessageProcessingException("No WeeklyPomodoroDto for period " + previousWeekPeriod);
        }

        return buildReportMessage(weeklyPomodoroDtoList.get(0), previousWeekPeriod);
    }

    private String buildReportMessage(WeeklyPomodoroDto weeklyPomodoroDto, PeriodDto period) {
        List<PomodoroDto> pomodoroWithoutToday = getPomodoroWithoutToday(weeklyPomodoroDto, period.getEnd());

        PomodoroStandardReportDto report = pomodoroStandardReporter.report(period, pomodoroWithoutToday);

        String header = "Report for: " + period.getStart().toLocalDate() + "  -  " + period.getEnd().toLocalDate() + "\n";

        return buildReportMessage(report, header);
    }

    private List<PomodoroDto> getPomodoroWithoutToday(WeeklyPomodoroDto weeklyPomodoroDto, LocalDateTime periodEnd) {
        List<PomodoroDto> pomodoro = weeklyPomodoroDto.getPomodoro();

        return pomodoro.stream()
                .filter(p -> !p.getEndTime().toLocalDate().atStartOfDay().isAfter(periodEnd))
                .collect(Collectors.toList());
    }

    private boolean isStartOfWeek(LocalDate today) {
        return DayOfWeek.MONDAY.equals(today.getDayOfWeek());
    }

}

package com.igorgorbunov3333.timer.service.message.pomodoro.report.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
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
        PeriodDto periodWithoutToday = getPeriodWithoutToday(weeklyPomodoroDto);
        List<PomodoroDto> pomodoroWithoutToday = getPomodoroWithoutToday(weeklyPomodoroDto, periodWithoutToday.getEnd());

        PomodoroStandardReportDto report = pomodoroStandardReporter.report(periodWithoutToday, pomodoroWithoutToday);

        String header = "Report for: " + periodWithoutToday.getStart().toLocalDate() + "  -  " + periodWithoutToday.getEnd().toLocalDate() + "\n";

        return buildReportMessage(report, header);
    }

    private PeriodDto getPeriodWithoutToday(WeeklyPomodoroDto weeklyPomodoroDto) {
        PeriodDto period = weeklyPomodoroDto.getPeriod();
        return new PeriodDto(period.getStart(), period.getEnd().minusDays(1L));
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

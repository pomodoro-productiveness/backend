package com.igorgorbunov3333.timer.console.service.pomodoro.provider;

import com.igorgorbunov3333.timer.console.rest.dto.DayOffDto;
import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.dayoff.DayOffComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.period.PomodoroByWeekDivider;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Component
@AllArgsConstructor
public class WeeklyPomodoroProvider {

    private final CurrentTimeComponent currentTimeComponent;
    private final DayOffComponent dayOffComponent;
    private final PomodoroByWeekDivider pomodoroByWeekDivider;
    private final PomodoroComponent pomodoroComponent;

    public WeeklyPomodoroDto provideCurrentWeekPomodoro() {
        List<PomodoroDto> weeklyPomodoro = provide();
        PeriodDto period = getCurrentPeriod();

        if (weeklyPomodoro.isEmpty()) {
            return WeeklyPomodoroDto.buildEmpty(period);
        }

        List<DailyPomodoroDto> weeklyPomodoroDto = provideWeeklyPomodoro(weeklyPomodoro, period);

        return new WeeklyPomodoroDto(weeklyPomodoroDto, period);
    }

    public List<WeeklyPomodoroDto> provideWeeklyPomodoroForPeriod(YearMonth month, List<PomodoroDto> pomodoro) {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();

        LocalDate periodEnd;
        if (YearMonth.from(today).isAfter(month)) {
            periodEnd = month.atEndOfMonth();
        } else {
            periodEnd = today;
        }

        PeriodDto monthPeriod = new PeriodDto(
                month.atDay(1).atStartOfDay(),
                periodEnd.atTime(LocalTime.MAX)
        );

        List<PeriodDto> weeks = pomodoroByWeekDivider.dividePeriodByWeeks(monthPeriod);

        List<WeeklyPomodoroDto> weeklyPomodoroDto = new ArrayList<>();
        for (PeriodDto currentWeek : weeks) {
            List<PomodoroDto> weeklyPomodoro = pomodoro.stream()
                    .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(currentWeek.getStart())
                            && !p.getEndTime().toLocalDateTime().isAfter(currentWeek.getEnd()))
                    .collect(Collectors.toList());

            List<DailyPomodoroDto> weeklyDailyPomodoro = provideWeeklyPomodoro(weeklyPomodoro, currentWeek);

            weeklyPomodoroDto.add(new WeeklyPomodoroDto(weeklyDailyPomodoro, currentWeek));
        }

        return weeklyPomodoroDto;
    }

    public List<DailyPomodoroDto> provideWeeklyPomodoro(List<PomodoroDto> weeklyPomodoro, PeriodDto period) {
        List<DayOffDto> dayOffs = dayOffComponent.getDayOffs(
                period.getStart().toLocalDate(),
                period.getEnd().toLocalDate()
        );
        List<LocalDate> dayOffsDates = dayOffs.stream()
                .map(DayOffDto::getDay)
                .toList();

        Map<LocalDate, List<PomodoroDto>> datesToPomodoro = new LinkedHashMap<>();
        for (PomodoroDto pomodoro : weeklyPomodoro) {
            LocalDate pomodoroDate = pomodoro.getStartTime().toLocalDate();
            List<PomodoroDto> datePomodoro = datesToPomodoro.get(pomodoroDate);

            if (CollectionUtils.isEmpty(datePomodoro)) {
                datePomodoro = new ArrayList<>();
            }

            datePomodoro.add(pomodoro);

            datesToPomodoro.put(pomodoroDate, datePomodoro);
        }

        Map<LocalDate, List<PomodoroDto>> datesToPomodoroWithDaysWithoutPomodoro =
                addDatesWithoutPomodoro(datesToPomodoro, period);

        List<DailyPomodoroDto> dailyPomodoro = new ArrayList<>();
        for (Map.Entry<LocalDate, List<PomodoroDto>> entry : datesToPomodoroWithDaysWithoutPomodoro.entrySet()) {
            boolean dayOff = dayOffsDates.contains(entry.getKey());
            DayOfWeek dayOfWeek = getDayOfWeek(entry.getKey());

            DailyPomodoroDto currentDailyPomodoro = new DailyPomodoroDto(
                    entry.getValue(),
                    dayOff,
                    dayOfWeek,
                    entry.getKey()
            );

            dailyPomodoro.add(currentDailyPomodoro);
        }

        return dailyPomodoro;
    }

    private PeriodDto getCurrentPeriod() {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();

        DayOfWeek currentWeekDays = getDayOfWeek(today);

        LocalDate startPeriod = today.minusDays(currentWeekDays.getValue() - 1);

        return new PeriodDto(startPeriod.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    private List<PomodoroDto> provide() {
        LocalDate startDayOfWeek = provideStartDayOfWeek();
        LocalDate end = currentTimeComponent.getCurrentDateTime()
                .toLocalDate();

        return pomodoroComponent.getPomodoro(startDayOfWeek, end, null);
    }

    private LocalDate provideStartDayOfWeek() {
        LocalDate currentDay = currentTimeComponent.getCurrentDateTime().toLocalDate();

        return currentDay.with(DayOfWeek.MONDAY);
    }

    private Map<LocalDate, List<PomodoroDto>> addDatesWithoutPomodoro(Map<LocalDate, List<PomodoroDto>> datesToPomodoro,
                                                                      PeriodDto period) {
        Map<LocalDate, List<PomodoroDto>> datesToPomodoroWithDaysWithoutPomodoro = new LinkedHashMap<>();

        LocalDate startDate = period.getStart().toLocalDate();
        LocalDate endDate = period.getEnd().toLocalDate();
        for (LocalDate current = startDate;
             current.isBefore(endDate.plusDays(1L));
             current = current.plusDays(1L)) {

            List<PomodoroDto> dailyPomodoro = datesToPomodoro.get(current);

            if (dailyPomodoro == null) {
                dailyPomodoro = new ArrayList<>();
            }

            datesToPomodoroWithDaysWithoutPomodoro.put(current, dailyPomodoro);
        }

        return datesToPomodoroWithDaysWithoutPomodoro;
    }

    private DayOfWeek getDayOfWeek(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        return DayOfWeek.of(dayOfWeek);
    }

}

package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.period.PomodoroByWeekDivider;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Service
@AllArgsConstructor
public class WeeklyPomodoroProvider implements BasePomodoroProvider {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final CurrentTimeService currentTimeService;
    private final TagService tagService;
    private final DayOffRepository dayOffRepository;
    private final PomodoroByWeekDivider pomodoroByWeekDivider;

    public WeeklyPomodoroDto provideWeeklyPomodoro() {
        List<PomodoroDto> weeklyPomodoro = provide();

        if (weeklyPomodoro.isEmpty()) {
            return WeeklyPomodoroDto.buildEmpty();
        }

        PeriodDto period = getCurrentPeriod();
        List<DailyPomodoroDto> weeklyPomodoroDto = provideWeeklyPomodoro(weeklyPomodoro, period);

        return new WeeklyPomodoroDto(weeklyPomodoroDto, period);
    }

    private List<PomodoroDto> provide() {
        LocalDate startDayOfWeek = provideStartDayOfWeek();

        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start = startDayOfWeek.atStartOfDay().atZone(currentZoneId);
        ZonedDateTime end = currentTimeService.getCurrentDateTime()
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(currentZoneId);

        return provide(start, end, null);
    }

    public List<WeeklyPomodoroDto> provideWeeklyPomodoroForPeriod(YearMonth month, List<PomodoroDto> pomodoro) {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

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

    private LocalDate provideStartDayOfWeek() {
        LocalDate currentDay = getCurrentTimeService().getCurrentDateTime().toLocalDate();

        return currentDay.with(DayOfWeek.MONDAY);
    }

    private List<DailyPomodoroDto> provideWeeklyPomodoro(List<PomodoroDto> weeklyPomodoro, PeriodDto period) {
        List<DayOff> dayOffs = dayOffRepository.findByDayGreaterThanEqualAndDayLessThanEqualOrderByDay(
                period.getStart().toLocalDate(),
                period.getEnd().toLocalDate()
        );
        List<LocalDate> dayOffsDates = dayOffs.stream()
                .map(DayOff::getDay)
                .collect(Collectors.toList());

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

    private PeriodDto getCurrentPeriod() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        DayOfWeek currentWeekDays = getDayOfWeek(today);

        LocalDate startPeriod = today.minusDays(currentWeekDays.getValue() - 1);

        return new PeriodDto(startPeriod.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    private DayOfWeek getDayOfWeek(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        return DayOfWeek.of(dayOfWeek);
    }

}

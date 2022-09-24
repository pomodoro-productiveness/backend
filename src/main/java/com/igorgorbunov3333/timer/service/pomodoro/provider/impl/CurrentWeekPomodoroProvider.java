package com.igorgorbunov3333.timer.service.pomodoro.provider.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.PomodoroProvider;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class CurrentWeekPomodoroProvider implements PomodoroProvider {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final CurrentTimeService currentTimeService;
    private final TagService tagService;
    private final DayOffRepository dayOffRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PomodoroDto> provide(String pomodoroTag) {
        LocalDate startDayOfWeek = provideStartDayOfWeek();

        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start = startDayOfWeek.atStartOfDay().atZone(currentZoneId);
        ZonedDateTime end = currentTimeService.getCurrentDateTime()
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(currentZoneId);

        return provide(start, end, pomodoroTag);
    }

    public WeeklyPomodoroDto provideWeeklyPomodoro() {
        List<PomodoroDto> weeklyPomodoro = provide(null);

        if (weeklyPomodoro.isEmpty()) {
            return WeeklyPomodoroDto.buildEmpty();
        }

        PeriodDto period = getCurrentPeriod();
        List<DailyPomodoroDto> weeklyPomodoroDto = provideWeeklyPomodoro(weeklyPomodoro, period);

        return new WeeklyPomodoroDto(weeklyPomodoroDto, period);
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

        List<DailyPomodoroDto> dailyPomodoro = new ArrayList<>();
        for (Map.Entry<LocalDate, List<PomodoroDto>> entry : datesToPomodoro.entrySet()) {
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

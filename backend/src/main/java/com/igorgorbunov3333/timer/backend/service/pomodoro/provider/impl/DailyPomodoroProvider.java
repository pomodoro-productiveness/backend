//package com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl;
//
//import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
//import com.igorgorbunov3333.timer.backend.model.entity.dayoff.DayOff;
//import com.igorgorbunov3333.timer.backend.repository.DayOffRepository;
//import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
//import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
//import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.BasePomodoroProvider;
//import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import org.springframework.data.util.Pair;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@AllArgsConstructor
//public class DailyPomodoroProvider implements BasePomodoroProvider {
//
//    private final CurrentTimeService currentTimeService;
//    @Getter
//    private final PomodoroRepository pomodoroRepository;
//    @Getter
//    private final PomodoroMapper pomodoroMapper;
//    private final DayOffRepository dayOffRepository;
//
//    public List<PomodoroDto> provideForCurrentDay(String tag) { //TODO: change to return DailyPomodoroDto instead
//        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();
//
//        return provide(startEndTimePair.getFirst(), startEndTimePair.getSecond(), tag);
//    }
//
//    public DailyPomodoroDto provide(LocalDate date) {
//        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
//        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
//
//        List<PomodoroDto> dailyPomodoro = provide(start, end, null);
//
//        Optional<DayOff> dayOffOptional = dayOffRepository.findByDay(date);
//
//        boolean dayOff = dayOffOptional.isPresent();
//
//        return new DailyPomodoroDto(dailyPomodoro, dayOff, date.getDayOfWeek(), date);
//    }
//
//}

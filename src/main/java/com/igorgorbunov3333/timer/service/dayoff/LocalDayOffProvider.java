package com.igorgorbunov3333.timer.service.dayoff;

import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class LocalDayOffProvider implements CurrentWeekDaysProvidable {

    private final DayOffRepository dayOffRepository;
    @Getter
    private final CurrentTimeService currentTimeService;

    public List<DayOff> provideCurrentWeekDayOffs() {
        LocalDate currentWeekStartDay = currentTimeService.getCurrentDateTime().toLocalDate()
                .minusDays(provideDaysOfCurrentWeek().size() - 1);

        return dayOffRepository.findByDayGreaterThanEqualOrderByDay(currentWeekStartDay);
    }

}

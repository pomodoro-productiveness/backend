package com.igorgorbunov3333.timer.service.pomodoro.work.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.WorkingPomodorosPerformanceRateDto;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.provider.MonthlyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MonthlyWorkingTimeStandardCalculator extends WorkingTimeStandardCalculator {

    @Getter
    private final CurrentTimeService currentTimeService;
    private final DayOffRepository dayOffRepository;
    @Getter
    private final PomodoroProperties pomodoroProperties;
    private final MonthlyLocalPomodoroProvider monthlyLocalPomodoroProvider;


    public WorkingPomodorosPerformanceRateDto calculate() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        YearMonth yearMonth = YearMonth.from(today);
        LocalDate startMonthDay = yearMonth.atDay(1);

        List<LocalDate> dayOffs = dayOffRepository.findByDayGreaterThanEqualOrderByDay(startMonthDay).stream()
                .map(DayOff::getDay)
                .collect(Collectors.toList());

        int workedPomodoroAmount = monthlyLocalPomodoroProvider.provide(pomodoroProperties.getTag().getWork())
                .size();

        return calculate(startMonthDay, dayOffs, workedPomodoroAmount);
    }

}

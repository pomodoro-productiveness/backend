package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.period.WeekStartDayProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Getter
@Service
@AllArgsConstructor
public class WeekEducationTimeStandardCalculator implements WeekStartDayProvidable, EducationTimeStandardCalculator {

    private final CurrentTimeService currentTimeService;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public int calculate(List<PomodoroDto> pomodoro) {
        LocalDate startOfWeek = provideStartDayOfWeek();

        return calculate(startOfWeek, pomodoro);
    }

    @Override
    public PomodoroPeriod period() {
        return PomodoroPeriod.WEEK;
    }

}

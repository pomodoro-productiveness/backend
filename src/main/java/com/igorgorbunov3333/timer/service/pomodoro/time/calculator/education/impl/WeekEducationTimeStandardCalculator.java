package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.period.WeekStartDayProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeekLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.CalculationPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class WeekEducationTimeStandardCalculator implements WeekStartDayProvidable, EducationTimeStandardCalculator {

    private final WeekLocalPomodoroProvider weekLocalPomodoroProvider;
    @Getter
    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroProperties pomodoroProperties;

    @Override
    public int calculate() {
        List<PomodoroDto> weekPomodoro = weekLocalPomodoroProvider.provideCurrentWeekPomodoros(pomodoroProperties.getTag().getEducation());
        LocalDate startOfWeek = provideStartDayOfWeek();

        return calculate(startOfWeek, weekPomodoro.size());
    }

    @Override
    public CalculationPeriod period() {
        return CalculationPeriod.WEEK;
    }

}

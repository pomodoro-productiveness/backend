package com.igorgorbunov3333.timer.service.pomodoro.time.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;

import java.util.List;

public interface BaseTimeStandardCalculator {

    CurrentTimeService getCurrentTimeService();

    PomodoroProperties getPomodoroProperties();

    PomodoroPeriod period();

    int calculate(List<PomodoroDto> pomodoro);

}

package com.igorgorbunov3333.timer.service.pomodoro.time.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.CalculationPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;

public interface BaseTimeStandardCalculator {

    CurrentTimeService getCurrentTimeService();

    PomodoroProperties getPomodoroProperties();

    CalculationPeriod period();

    int calculate();

}

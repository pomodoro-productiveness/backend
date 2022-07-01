package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;

import java.util.List;

public interface PomodoroProvider extends BasePomodoroProvider {

    PomodoroPeriod pomodoroPeriod();

    List<PomodoroDto> provide(String tag);

}

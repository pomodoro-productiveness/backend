package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.PomodoroDtoV2;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public interface PomodoroPeriodService {

    Map<DayOfWeek, List<PomodoroDtoV2>> getCurrentWeekPomodoros();

}

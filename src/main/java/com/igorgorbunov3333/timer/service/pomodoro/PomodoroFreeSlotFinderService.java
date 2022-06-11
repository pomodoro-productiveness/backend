package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;

import java.util.List;

public interface PomodoroFreeSlotFinderService {

    PeriodDto findFreeSlotInCurrentDay(List<PomodoroDto> dailyPomodoros);

}

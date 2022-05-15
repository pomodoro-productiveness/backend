package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;

public interface PomodoroFreeSlotFinderService {

    PeriodDto findFreeSlotInCurrentDay();

}

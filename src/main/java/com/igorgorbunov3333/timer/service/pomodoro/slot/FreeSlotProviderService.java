package com.igorgorbunov3333.timer.service.pomodoro.slot;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;

import java.util.List;

public interface FreeSlotProviderService {

    PeriodDto findFreeSlotInCurrentDay(List<PeriodDto> dailyPomodoro);

}

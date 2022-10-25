package com.igorgorbunov3333.timer.backend.service.pomodoro.slot;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;

import java.util.List;

public interface FreeSlotProviderService {

    PeriodDto findFreeSlotInCurrentDay(List<PeriodDto> dailyPomodoro);

}

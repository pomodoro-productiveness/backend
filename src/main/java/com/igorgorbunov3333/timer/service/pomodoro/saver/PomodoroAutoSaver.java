package com.igorgorbunov3333.timer.service.pomodoro.saver;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroFreeSlotFinderService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class PomodoroAutoSaver implements SinglePomodoroSavable{

    @Getter
    private final PomodoroRepository pomodoroRepository;
    private final PomodoroFreeSlotFinderService pomodoroFreeSlotFinderService;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;

    @Transactional
    public PomodoroDto save() {
        List<PomodoroDto> dailyPomodoros = currentDayLocalPomodoroProvider.provide(null);
        PeriodDto latestFreeSlot = pomodoroFreeSlotFinderService.findFreeSlotInCurrentDay(dailyPomodoros);
        Pomodoro pomodoroToSave = buildPomodoro(latestFreeSlot);

        return save(pomodoroToSave);
    }

    private Pomodoro buildPomodoro(PeriodDto latestFreeSlot) {
        ZonedDateTime pomodoroStartTime = latestFreeSlot.getStart()
                .truncatedTo(ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault());
        ZonedDateTime pomodoroEndTime = latestFreeSlot.getEnd()
                .truncatedTo(ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault());

        return new Pomodoro(null, pomodoroStartTime, pomodoroEndTime, true, List.of(), null);
    }

}

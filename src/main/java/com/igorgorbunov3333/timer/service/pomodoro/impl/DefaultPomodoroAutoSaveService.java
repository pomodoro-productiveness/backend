package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroAutoSaveService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroFreeSlotFinderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class DefaultPomodoroAutoSaveService implements PomodoroAutoSaveService {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroFreeSlotFinderService pomodoroFreeSlotFinderService;
    private final PomodoroMapper pomodoroMapper;

    @Override
    @Transactional
    public PomodoroDto save() {
        PeriodDto latestFreeSlot = pomodoroFreeSlotFinderService.findFreeSlotInCurrentDay();
        Pomodoro pomodoroToSave = buildPomodoro(latestFreeSlot);

        Pomodoro savedPomodoro = pomodoroRepository.save(pomodoroToSave);
        return pomodoroMapper.mapToDto(savedPomodoro);
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

package com.igorgorbunov3333.timer.service.pomodoro.saver;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.slot.FreeSlotProviderService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PomodoroAutoSaver implements SinglePomodoroSavable{

    @Getter
    private final PomodoroRepository pomodoroRepository;
    private final FreeSlotProviderService freeSlotProviderService;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;

    @Transactional
    public List<PomodoroDto> save(int numberToSave) {
        List<PomodoroDto> dailyPomodoro = currentDayLocalPomodoroProvider.provide(null);
        List<PeriodDto> reservedSlots = dailyPomodoro.stream()
                .map(p -> new PeriodDto(p.getStartTime().toLocalDateTime(), p.getEndTime().toLocalDateTime()))
                .collect(Collectors.toList());

        List<PomodoroDto> savedPomodoro = new ArrayList<>();
        for (int i = 0; i < numberToSave; i++) {
            PeriodDto latestFreeSlot = freeSlotProviderService.findFreeSlotInCurrentDay(reservedSlots);

            if (latestFreeSlot == null) {
                return List.of();
            }

            Pomodoro pomodoroToSave = buildPomodoro(latestFreeSlot);
            reservedSlots.add(latestFreeSlot);

            PomodoroDto savedSinglePomodoro = save(pomodoroToSave);

            savedPomodoro.add(savedSinglePomodoro);
        }

        return savedPomodoro;
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

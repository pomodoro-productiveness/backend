package com.igorgorbunov3333.timer.backend.service.pomodoro.saver;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroAutoSaveResponseDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.pomodoro.slot.FreeSlotProviderService;
import com.igorgorbunov3333.timer.backend.service.pomodoro.updater.PomodoroUpdater;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PomodoroAutoSaver {

    private final PomodoroRepository pomodoroRepository;
    private final FreeSlotProviderService freeSlotProviderService;
    private final PomodoroMapper pomodoroMapper;
    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;
    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final PomodoroUpdater pomodoroUpdater;

    @Transactional
    public PomodoroAutoSaveResponseDto save(int numberToSave, long groupId) {
        Optional<PomodoroTagGroup> pomodoroTagGroupOptional = pomodoroTagGroupRepository.findById(groupId);

        if (pomodoroTagGroupOptional.isEmpty()) {
            throw new EntityDoesNotExist(String.format("No PomodoroTagGroupId [%d]", groupId));
        }

        List<PomodoroDto> dailyPomodoro = currentDayLocalPomodoroProvider.provideForCurrentDay(null);
        List<PeriodDto> reservedSlots = dailyPomodoro.stream()
                .map(p -> new PeriodDto(p.getStartTime().toLocalDateTime(), p.getEndTime().toLocalDateTime()))
                .collect(Collectors.toList());

        List<PomodoroDto> savedPomodoro = new ArrayList<>();
        for (int i = 0; i < numberToSave; i++) {
            PeriodDto latestFreeSlot = freeSlotProviderService.findFreeSlotInCurrentDay(reservedSlots, null);

            Pomodoro pomodoroToSave = buildPomodoro(latestFreeSlot);
            reservedSlots.add(latestFreeSlot);

            Pomodoro pomodoro = pomodoroRepository.save(pomodoroToSave);

            PomodoroDto pomodoroDto = pomodoroMapper.toDto(pomodoro);

            savedPomodoro.add(pomodoroDto);
        }

        Set<String> tagsToMapToPomodoro = pomodoroTagGroupOptional.get().getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .collect(Collectors.toSet());

        pomodoroUpdater.updatePomodoroWithTag(savedPomodoro.stream().map(PomodoroDto::getId).toList(), tagsToMapToPomodoro);

        return new PomodoroAutoSaveResponseDto(savedPomodoro);
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

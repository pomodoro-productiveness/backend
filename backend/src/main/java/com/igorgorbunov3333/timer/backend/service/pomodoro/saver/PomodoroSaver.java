package com.igorgorbunov3333.timer.backend.service.pomodoro.saver;

import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroPauseMapper;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroSaveRequestDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroPause;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.backend.service.exception.NoDataException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@AllArgsConstructor
public class PomodoroSaver {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final PomodoroPauseMapper pomodoroPauseMapper;

    public PomodoroDto save(@NonNull PomodoroSaveRequestDto saveRequest) {
        List<PomodoroPause> pauses = pomodoroPauseMapper.toEntities(saveRequest.getPauses());

        PomodoroTagGroup pomodoroTagGroup = pomodoroTagGroupRepository.findById(saveRequest.getTagGroupId())
                .orElse(null);

        if (pomodoroTagGroup == null) {
            throw new NoDataException(String.format("%s with id [%d] is not exists", PomodoroTagGroup.class.getSimpleName(), saveRequest.getTagGroupId()));
        }

        Pomodoro pomodoro = Pomodoro.builder()
                .setStartTime(saveRequest.getStart())
                .setEndTime(saveRequest.getEnd())
                .setPomodoroPauses(pauses)
                .setPomodoroTagGroup(pomodoroTagGroup)
                .build();

        Pomodoro savedPomodoro = pomodoroRepository.save(pomodoro);

        return pomodoroMapper.toDto(savedPomodoro);
    }

}

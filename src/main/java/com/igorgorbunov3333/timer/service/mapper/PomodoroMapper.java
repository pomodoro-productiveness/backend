package com.igorgorbunov3333.timer.service.mapper;

import com.igorgorbunov3333.timer.model.dto.PomodoroDtoV2;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PomodoroMapper {

    public List<PomodoroDtoV2> mapToDto(List<Pomodoro> pomodoros) {
        return pomodoros.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PomodoroDtoV2 mapToDto(Pomodoro pomodoro) {
        return new PomodoroDtoV2(pomodoro.getId(), pomodoro.getStartTime(), pomodoro.getEndTime());
    }

}

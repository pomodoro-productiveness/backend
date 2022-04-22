package com.igorgorbunov3333.timer.service.mapper;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PomodoroMapper {

    public List<PomodoroDto> mapToDto(List<Pomodoro> pomodoros) {
        return pomodoros.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PomodoroDto mapToDto(Pomodoro pomodoro) {
        return new PomodoroDto(
                pomodoro.getId(),
                pomodoro.getStartTime(),
                pomodoro.getEndTime()
        );
    }

}

package com.igorgorbunov3333.timer.backend.service.pomodoro.saver;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;

public interface SinglePomodoroSavable {

    PomodoroRepository getPomodoroRepository();
    PomodoroMapper getPomodoroMapper();

    default PomodoroDto save(Pomodoro pomodoro) {
        Pomodoro savedPomodoro = getPomodoroRepository().save(pomodoro);
        return getPomodoroMapper().mapToDto(savedPomodoro);
    }

}

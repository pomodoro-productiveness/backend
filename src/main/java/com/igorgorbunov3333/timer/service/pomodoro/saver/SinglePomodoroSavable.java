package com.igorgorbunov3333.timer.service.pomodoro.saver;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;

public interface SinglePomodoroSavable {

    PomodoroRepository getPomodoroRepository();
    PomodoroMapper getPomodoroMapper();

    default PomodoroDto save(Pomodoro pomodoro) {
        Pomodoro savedPomodoro = getPomodoroRepository().save(pomodoro);
        return getPomodoroMapper().mapToDto(savedPomodoro);
    }

}

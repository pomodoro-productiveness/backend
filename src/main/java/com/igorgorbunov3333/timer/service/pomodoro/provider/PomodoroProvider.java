package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;

import java.util.List;

public interface PomodoroProvider extends BasePomodoroProvider {

    List<PomodoroDto> provide(String tag);

}

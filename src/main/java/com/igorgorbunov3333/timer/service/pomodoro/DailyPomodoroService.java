package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;

import java.util.List;

public interface DailyPomodoroService {

    List<PomodoroDto> getDailyPomodoros();

}

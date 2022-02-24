package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PomodoroService {

    PomodoroDto saveByDuration(int pomodoroDuration);

    long getPomodorosInDay();

    List<PomodoroDto> getPomodorosInDayExtended();

    Map<LocalDate, List<PomodoroDto>> getMonthlyPomodoros();

    void removePomodoro(Long id);

    Long removeLatest();

    PomodoroDto saveAutomatically();

}

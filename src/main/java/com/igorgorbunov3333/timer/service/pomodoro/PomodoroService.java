package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.model.entity.PomodoroTag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PomodoroService {

    PomodoroDto saveByDurationWithPauses(int pomodoroDuration, List<PomodoroPauseDto> pomodoroPauses);

    PomodoroDto saveByDuration(int pomodoroDuration);

    long getPomodorosInDay();

    List<PomodoroDto> getPomodorosInDayExtended();

    Map<LocalDate, List<PomodoroDto>> getMonthlyPomodoros();

    void removePomodoro(Long id);

    Long removeLatest();

    PomodoroDto saveAutomatically();

    void updatePomodoroWithTag(Long pomodoroId, String tagName);

    List<PomodoroDto> getAllSortedPomodoros();

    void removeAllPomodoros();

    void save(List<PomodoroDto> pomodoros, List<PomodoroTag> tags);
}

package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.AnalyzedWeekDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDtoV2;
import com.igorgorbunov3333.timer.service.exception.NoDataException;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroPeriodService;
import com.igorgorbunov3333.timer.service.pomodoro.WorkTimeAnalyzerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DefaultWorkTimeAnalyzerService implements WorkTimeAnalyzerService {

    private final PomodoroPeriodService pomodoroPeriodService;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public AnalyzedWeekDto getCurrentWeekPomodoroInfo() {
        Map<DayOfWeek, List<PomodoroDtoV2>> dayOfWeekToPomodoros = pomodoroPeriodService.getCurrentWeekPomodoros();
        if (dayOfWeekToPomodoros.isEmpty()) {
            throw new NoDataException("No weekly pomodoros available");
        }
        int pomodorosToFinalize = 0;
        int pomodorosOverworked = 0;
        int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        for (int i = 0; i < currentDayOfWeek; i++) {
            List<PomodoroDtoV2> currentDayPomodoros = dayOfWeekToPomodoros.get(DayOfWeek.of(i));
            if (!currentDayPomodoros.isEmpty()) {
                int pomodorosFinalizedAtDay = currentDayPomodoros.size();
                int pomodorosNeedToFinalizeAtDay = pomodoroProperties.getPomodorosPerDay();
                int difference = pomodorosFinalizedAtDay - pomodorosNeedToFinalizeAtDay;
                if (difference > 0) {
                    pomodorosOverworked += difference;
                } else if (difference < 0) {
                    pomodorosToFinalize += Math.abs(difference);
                }
            }
            if (pomodorosOverworked != pomodorosToFinalize) {
                if (pomodorosToFinalize > pomodorosOverworked) {
                    pomodorosToFinalize -= pomodorosOverworked;
                } else {
                    pomodorosOverworked -= pomodorosToFinalize;
                }
            }
        }
        return new AnalyzedWeekDto(pomodorosToFinalize, pomodorosOverworked);
    }

}

package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.AnalyzedWeekDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.exception.NoDataException;
import com.igorgorbunov3333.timer.service.pomodoro.WorkTimeAnalyzerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultWorkTimeAnalyzerService implements WorkTimeAnalyzerService {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroProperties pomodoroProperties;

    @Override
    public AnalyzedWeekDto pomodorosToFinalizeDuringCurrentWeek() {
        LocalDate currentDay = LocalDate.now();
        int currentDayOfWeek = currentDay.getDayOfWeek().getValue();
        LocalDate dayAtStartOfWeek = currentDay.minusDays(currentDayOfWeek);

        List<Pomodoro> weeklyPomodoros =
                pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(dayAtStartOfWeek.atStartOfDay(), currentDay.plusDays(1).atStartOfDay());
        if (weeklyPomodoros.isEmpty()) {
            throw new NoDataException("No weekly pomodoros available");
        }
        Map<Integer, List<Pomodoro>> dayOfWeekToPomodoros = weeklyPomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getDayOfWeek().getValue()));
        int pomodorosToFinalize = 0;
        int pomodorosOverworked = 0;
        for (int i = 0; i < currentDayOfWeek; i++) {
            List<Pomodoro> currentDayPomodoros = dayOfWeekToPomodoros.get(i);
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

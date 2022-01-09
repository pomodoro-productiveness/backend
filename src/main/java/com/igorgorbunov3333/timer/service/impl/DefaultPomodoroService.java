package com.igorgorbunov3333.timer.service.impl;

import com.igorgorbunov3333.timer.model.PomodoroState;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.PomodoroService;
import com.igorgorbunov3333.timer.service.SecondsFormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class DefaultPomodoroService implements PomodoroService {

    private static final int SECONDS_IN_20_MINUTES = 1200;
    private static final LocalDateTime START_DAY_TIMESTAMP = LocalDate.now().atStartOfDay();
    private static final LocalDateTime END_DAY_TIMESTAMP = LocalDate.now().atTime(LocalTime.MAX);

    @Autowired
    private DefaultAudioPlayerService player;
    @Autowired
    private PomodoroState pomodoroState;
    @Autowired
    private PomodoroRepository pomodoroRepository;
    @Autowired
    private SecondsFormatterService secondsFormatterService;

    @Async
    @Override
    public void starPomodoro() {
        if (pomodoroState.isCurrentlyRunning()) {
            return;
        }
        pomodoroState.isRunning(true);
        pomodoroState.setSeconds(0);
        for (int i = 0; i < SECONDS_IN_20_MINUTES; i++) {
            if (!pomodoroState.isCurrentlyRunning()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
            pomodoroState.setSeconds(pomodoroState.getSeconds() + 1);
        }
        if (pomodoroState.isCurrentlyRunning()) {
            player.play();
        }
    }

    @Override
    public void stopPomodoro() {
        pomodoroState.isRunning(false);
        player.stop();

        LocalDateTime endTime = LocalDateTime.now();
        int secondsPassed = pomodoroState.getSeconds();
        LocalDateTime startTime = endTime.minusSeconds(secondsPassed);
        Pomodoro pomodoro = new Pomodoro(startTime, endTime);
        pomodoroRepository.save(pomodoro);
        pomodoroState.setSeconds(0);
    }

    @Override
    public int getPomodoroTime() {
        return pomodoroState.getSeconds();
    }

    @Override
    public long getPomodorosInDay() {
        return pomodoroRepository.countByStartTimeAfterAndEndTimeBefore(START_DAY_TIMESTAMP, END_DAY_TIMESTAMP);
    }

    @Override
    public List<Pomodoro> getPomodorosInDayExtended() {
        return pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(START_DAY_TIMESTAMP, END_DAY_TIMESTAMP);
    }

    @Override
    public Map<LocalDate, List<Pomodoro>> getPomodorosInMonthExtended() {
        LocalDateTime monthAgoStartTimestamp = START_DAY_TIMESTAMP.minusMonths(1);
        List<Pomodoro> pomodoros = pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(monthAgoStartTimestamp, END_DAY_TIMESTAMP);
        Map<LocalDate, List<Pomodoro>> pomodorosByDates = pomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().toLocalDate()));
        return new TreeMap<>(pomodorosByDates);
    }

}

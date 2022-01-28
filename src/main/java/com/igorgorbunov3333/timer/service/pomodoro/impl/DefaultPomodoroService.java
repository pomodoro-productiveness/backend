package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPomodoroService implements PomodoroService {

    private static final LocalDateTime START_DAY_TIMESTAMP = LocalDate.now().atStartOfDay();
    private static final LocalDateTime END_DAY_TIMESTAMP = LocalDate.now().atTime(LocalTime.MAX);

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroEngine pomodoroEngine;

    @Override
    public void starPomodoro() {
        pomodoroEngine.startPomodoro();
    }

    @Override
    public void stopPomodoro() {
        Pomodoro pomodoro = buildPomodoro();
        long pomodoroMinimumLifetime = pomodoroEngine.getPomodoroCurrentDuration();
        if (pomodoroMinimumLifetime == 0) {
            System.out.println("Pomodoro lifetime didn't set. Please configure");
        } else if (pomodoro.getStartEndTimeDifferenceInSeconds() < pomodoroMinimumLifetime) {
            System.out.println("Pomodoro lifetime is less then [" + pomodoroMinimumLifetime + "] seconds");
            return;
        }
        pomodoroEngine.stopPomodoro();
        pomodoroRepository.save(pomodoro);
    }

    @Override
    public int getPomodoroCurrentDuration() {
        return pomodoroEngine.getPomodoroCurrentDuration();
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

    @Override
    public void removePomodoro(Long id) {
        Optional<Pomodoro> pomodoroOptional = pomodoroRepository.findById(id);
        if (pomodoroOptional.isEmpty()) {
            System.out.println("No such pomodoro with id [" + id + "]");
            return;
        }
        Pomodoro pomodoro = pomodoroOptional.get();
        LocalDate pomodoroLocalDate = pomodoro.getStartTime().toLocalDate();
        if (pomodoroLocalDate.isBefore(LocalDate.now())) {
            System.out.println("Pomodoro with id [" + id + "] cannot be deleted because pomodoro not from todays day");
            return;
        }
        pomodoroRepository.deleteById(id);
        System.out.println("Pomodoro with id [" + id + "] removed");
    }

    @Override
    public void save() {
        Optional<Pomodoro> latestPomodoroOptional = pomodoroRepository.findTopByOrderByEndTimeDesc();
        LocalDateTime latestPomodoroEndTime = latestPomodoroOptional
                .map(Pomodoro::getEndTime)
                .orElse(null);

        LocalDateTime newPomodoroEndTime = LocalDateTime.now().minusMinutes(1L);
        if (latestPomodoroEndTime == null) {
            Pomodoro pomodoroToSave = new Pomodoro(null, newPomodoroEndTime.minusMinutes(20L), newPomodoroEndTime);
            pomodoroRepository.save(pomodoroToSave);
            return;
        }
        long newPomodoroEndEpochSeconds = newPomodoroEndTime.toEpochSecond(ZoneOffset.UTC);

        LocalDateTime latestPomodoroEndTimePlusMinute = latestPomodoroEndTime.plusMinutes(1L);
        long latestPomodoroEndtEpochSeconds = latestPomodoroEndTimePlusMinute.toEpochSecond(ZoneOffset.UTC);
        long secondsDifference = newPomodoroEndEpochSeconds - latestPomodoroEndtEpochSeconds;

        if (secondsDifference <= 60 * 20) {
            System.out.println("Cannot save pomodoro automatically due to less than 20 minutes have passed since the end of the previous pomodoro");
            return;
        }

        Pomodoro pomodoroToSave = new Pomodoro(null, newPomodoroEndTime.minusMinutes(20L), newPomodoroEndTime);
        Pomodoro savedPomodoro = pomodoroRepository.save(pomodoroToSave);
        System.out.println("Pomodoro saved: " + savedPomodoro);
    }

    @Override
    public boolean isNotActive() {
        return !pomodoroEngine.isPomodoroCurrentlyRunning();
    }

    private Pomodoro buildPomodoro() {
        LocalDateTime endTime = LocalDateTime.now();
        int pomodoroDuration = pomodoroEngine.getPomodoroCurrentDuration();
        LocalDateTime startTime = endTime.minusSeconds(pomodoroDuration);
        return new Pomodoro(null, startTime, endTime);
    }

}

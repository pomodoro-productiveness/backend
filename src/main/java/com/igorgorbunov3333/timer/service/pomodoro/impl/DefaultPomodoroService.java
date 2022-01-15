package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.PomodoroState;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPomodoroService implements PomodoroService {

    private static final int SECONDS_IN_20_MINUTES = 1200;
    private static final LocalDateTime START_DAY_TIMESTAMP = LocalDate.now().atStartOfDay();
    private static final LocalDateTime END_DAY_TIMESTAMP = LocalDate.now().atTime(LocalTime.MAX);

    private final AudioPlayerService player;
    private final PomodoroState pomodoroState;
    private final PomodoroRepository pomodoroRepository;
    private final PomodoroProperties pomodoroProperties;

    @Async
    @Override
    public void starPomodoro() {
        if (pomodoroState.isRunning()) {
            return;
        }
        pomodoroState.setRunning(true);
        pomodoroState.setSeconds(0);
        do {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
            pomodoroState.setSeconds(pomodoroState.getSeconds() + 1);
            if (pomodoroState.getSeconds() >= SECONDS_IN_20_MINUTES) {
                player.play();
            }
        } while (pomodoroState.isRunning());
    }

    @Override
    public void stopPomodoro() {
        pomodoroState.setRunning(false);
        player.stop();

        LocalDateTime endTime = LocalDateTime.now();
        int secondsPassed = pomodoroState.getSeconds();
        LocalDateTime startTime = endTime.minusSeconds(secondsPassed);
        Pomodoro pomodoro = new Pomodoro(null, startTime, endTime);
        Long pomodoroActualLifetime = pomodoro.getStartEndTimeDifferenceInSeconds();
        Long pomodoroMinimumLifetime = pomodoroProperties.getMinimumLifetime();
        if (pomodoroActualLifetime < pomodoroMinimumLifetime) {
            System.out.println("Pomodoro lifetime is less then [" + pomodoroMinimumLifetime + "] seconds");
            return;
        }
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

}

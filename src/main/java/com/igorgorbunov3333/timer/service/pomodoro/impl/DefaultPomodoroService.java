package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.exception.NoDataException;
import com.igorgorbunov3333.timer.service.exception.PomodoroCrudException;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroSynchronizationScheduler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class DefaultPomodoroService implements PomodoroService {

    private static final ZonedDateTime START_DAY_TIMESTAMP = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
    private static final ZonedDateTime END_DAY_TIMESTAMP = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final PomodoroSynchronizationScheduler pomodoroSynchronizationScheduler;

    @Override
    public PomodoroDto saveByDuration(int pomodoroDuration) {
        Pomodoro pomodoro = buildPomodoro(pomodoroDuration);
        Pomodoro savedPomodoro = pomodoroRepository.save(pomodoro);
        pomodoroSynchronizationScheduler.addUpdateJob(savedPomodoro.getEndTime().toLocalDateTime());
        return pomodoroMapper.mapToDto(savedPomodoro);
    }

    @Override
    public long getPomodorosInDay() {
        return pomodoroRepository.countByStartTimeAfterAndEndTimeBefore(START_DAY_TIMESTAMP, END_DAY_TIMESTAMP);
    }

    @Override
    public List<PomodoroDto> getPomodorosInDayExtended() {
        List<Pomodoro> pomodoros = pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(START_DAY_TIMESTAMP, END_DAY_TIMESTAMP);
        return pomodoroMapper.mapToDto(pomodoros);
    }

    @Override
    public Map<LocalDate, List<PomodoroDto>> getMonthlyPomodoros() {
        ZonedDateTime monthAgoStartTimestamp = START_DAY_TIMESTAMP.minusMonths(1);
        List<Pomodoro> pomodoros = pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(monthAgoStartTimestamp, END_DAY_TIMESTAMP);
        List<PomodoroDto> pomodoroDtos = pomodoroMapper.mapToDto(pomodoros);
        Map<LocalDate, List<PomodoroDto>> pomodorosByDates = pomodoroDtos.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().toLocalDate()));
        return new TreeMap<>(pomodorosByDates);
    }

    @Override
    public void removePomodoro(Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new NoDataException("No such pomodoro with id [" + pomodoroId + "]"));
        LocalDate pomodoroLocalDate = pomodoro.getStartTime().toLocalDate();
        if (pomodoroLocalDate.isBefore(LocalDate.now())) {
            throw new PomodoroCrudException("Pomodoro with id [" + pomodoroId + "] cannot be deleted because pomodoro not from todays day");
        }
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
        pomodoroSynchronizationScheduler.addRemovalJob();
    }

    @Override
    public Long removeLatest() {
        List<PomodoroDto> dailyPomodoros = getPomodorosInDayExtended();
        if (dailyPomodoros.isEmpty()) {
            throw new NoDataException("No daily pomodoros");
        }
        PomodoroDto latestDto = dailyPomodoros.get(dailyPomodoros.size() - 1);
        Long pomodoroId = latestDto.getId();
        pomodoroRepository.deleteById(pomodoroId);
        pomodoroRepository.flush();
        pomodoroSynchronizationScheduler.addRemovalJob();
        return pomodoroId;
    }

    @Override
    public PomodoroDto saveAutomatically() {
        Optional<Pomodoro> latestPomodoroOptional = pomodoroRepository.findTopByOrderByEndTimeDesc();
        ZonedDateTime latestPomodoroEndTime = latestPomodoroOptional
                .map(Pomodoro::getEndTime)
                .orElse(null);

        ZonedDateTime newPomodoroEndTime = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .minusMinutes(1L)
                .truncatedTo(ChronoUnit.SECONDS);
        if (latestPomodoroEndTime == null) {
            Pomodoro pomodoroToSave = new Pomodoro(null, newPomodoroEndTime.minusMinutes(20L), newPomodoroEndTime, true);
            pomodoroRepository.save(pomodoroToSave);
            return null;
        }
        long newPomodoroEndEpochSeconds = newPomodoroEndTime.toEpochSecond();

        ZonedDateTime latestPomodoroEndTimePlusMinute = latestPomodoroEndTime.plusMinutes(1L);
        long latestPomodoroEndtEpochSeconds = latestPomodoroEndTimePlusMinute.toEpochSecond();
        long secondsDifference = newPomodoroEndEpochSeconds - latestPomodoroEndtEpochSeconds;

        if (secondsDifference <= 60 * 20) {
            throw new PomodoroCrudException("Cannot save pomodoro automatically due to less than 20 minutes have " +
                    "passed since the end of the previous pomodoro");
        }

        Pomodoro pomodoroToSave = new Pomodoro(null, newPomodoroEndTime.minusMinutes(20L), newPomodoroEndTime, true);
        Pomodoro savedPomodoro = pomodoroRepository.save(pomodoroToSave);
        pomodoroSynchronizationScheduler.addUpdateJob(savedPomodoro.getEndTime().toLocalDateTime());
        return pomodoroMapper.mapToDto(savedPomodoro);
    }

    private Pomodoro buildPomodoro(int pomodoroSecondsDuration) {
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime endTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .atZone(currentZoneId);
        ZonedDateTime startTime = endTime.minusSeconds(pomodoroSecondsDuration);
        return new Pomodoro(null, startTime, endTime, false);
    }

}

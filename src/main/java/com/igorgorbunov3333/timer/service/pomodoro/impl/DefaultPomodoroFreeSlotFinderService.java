package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.DailyPomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroFreeSlotFinderService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DefaultPomodoroFreeSlotFinderService implements PomodoroFreeSlotFinderService {

    private static final long BREAK_BETWEEN_SLOTS = 1L;

    private final DailyPomodoroService dailyPomodoroService;
    private final CurrentTimeService currentTimeService;

    @Override
    public PeriodDto findFreeSlotInCurrentDay() {
        LocalDateTime currentTime = currentTimeService.getCurrentDateTime();

        PeriodDto potentialFreeSlot = buildPomodoroTimePeriodFromCurrentTime(currentTime);
        validatePeriod(potentialFreeSlot.getStart(), currentTime);

        List<PomodoroDto> reversedDailyPomodoros = getReversedDailyPomodoros();
        if (reversedDailyPomodoros.isEmpty()) {
            return potentialFreeSlot;
        }

        return findNearestFreeSlotConsideringExistingPomodoros(currentTime, potentialFreeSlot, reversedDailyPomodoros);
    }

    private PeriodDto buildPomodoroTimePeriodFromCurrentTime(LocalDateTime currentTime) {
        LocalDateTime periodStart = currentTime.minusMinutes(BREAK_BETWEEN_SLOTS)
                .minusMinutes(PomodoroChronoUtil.POMODORO_DURATION);
        LocalDateTime periodEnd = periodStart.plusMinutes(PomodoroChronoUtil.POMODORO_DURATION);

        return new PeriodDto(periodStart, periodEnd);
    }

    private List<PomodoroDto> getReversedDailyPomodoros() {
        List<PomodoroDto> dailyPomodoros = dailyPomodoroService.getDailyPomodoros();
        dailyPomodoros.sort(Comparator.comparing(PomodoroDto::getStartTime).reversed());

        return dailyPomodoros;
    }

    private PeriodDto findNearestFreeSlotConsideringExistingPomodoros(LocalDateTime currentTime,
                                                                      PeriodDto potentialFreeSlotPeriod,
                                                                      List<PomodoroDto> reversedDailyPomodoros) {
        if (isPotentialFreeSlotStartAfterLatestPomodoroEndPlusOneMinute(potentialFreeSlotPeriod.getStart(), reversedDailyPomodoros)) {
            return potentialFreeSlotPeriod;
        }

        PeriodDto latestFreeSlotAtNearestValidGap = tryToFindLatestFreeSlotAtNearestValidGapBetweenPomodoros(reversedDailyPomodoros);

        return Objects.requireNonNullElseGet(
                latestFreeSlotAtNearestValidGap,
                () -> tryToFindFreeSlotBeforeOldestPomodoroOrThrow(currentTime, reversedDailyPomodoros)
        );
    }

    private boolean isPotentialFreeSlotStartAfterLatestPomodoroEndPlusOneMinute(LocalDateTime potentialFreeSlotStartTime,
                                                                                List<PomodoroDto> reversedDailyPomodoros) {
        LocalDateTime latestPomodoroEndTime = reversedDailyPomodoros.get(0)
                .getEndTime()
                .toLocalDateTime();
        LocalDateTime oneMinuteAfterLatestPomodoroEndTime = latestPomodoroEndTime.plusMinutes(BREAK_BETWEEN_SLOTS);

        return potentialFreeSlotStartTime.isAfter(oneMinuteAfterLatestPomodoroEndTime);
    }

    private PeriodDto tryToFindLatestFreeSlotAtNearestValidGapBetweenPomodoros(List<PomodoroDto> reversedDailyPomodoros) {
        List<PeriodDto> gapsBetweenPomodoros = findGapsBetweenPomodoros(reversedDailyPomodoros);

        return gapsBetweenPomodoros.stream()
                .filter(this::isValidGap)
                .map(this::mapValidGapToPeriod)
                .findFirst()
                .orElse(null);
    }

    private List<PeriodDto> findGapsBetweenPomodoros(List<PomodoroDto> pomodoros) {
        PomodoroDto previousPomodoro = null;
        LocalDateTime start;
        LocalDateTime end;
        List<PeriodDto> gaps = new LinkedList<>();

        for (PomodoroDto currentPomodoro : pomodoros) {
            if (previousPomodoro != null) {
                start = currentPomodoro.getEndTime().toLocalDateTime();
                end = previousPomodoro.getStartTime().toLocalDateTime();
                gaps.add(new PeriodDto(start, end));
            }
            previousPomodoro = currentPomodoro;
        }

        return gaps;
    }

    private boolean isValidGap(PeriodDto gap) {
        LocalDateTime gapStartTime = gap.getStart();
        LocalDateTime gapEndTime = gap.getEnd();
        long necessaryPeriodDuration = PomodoroChronoUtil.POMODORO_DURATION + (2 * BREAK_BETWEEN_SLOTS);

        return ChronoUnit.MINUTES.between(gapStartTime, gapEndTime) > necessaryPeriodDuration;
    }

    private PeriodDto mapValidGapToPeriod(PeriodDto gap) {
        LocalDateTime periodStart = gap.getEnd()
                .minusMinutes(BREAK_BETWEEN_SLOTS)
                .minusMinutes(PomodoroChronoUtil.POMODORO_DURATION);
        LocalDateTime periodEnd = gap.getEnd()
                .minusMinutes(BREAK_BETWEEN_SLOTS);

        return new PeriodDto(periodStart, periodEnd);
    }

    private PeriodDto tryToFindFreeSlotBeforeOldestPomodoroOrThrow(LocalDateTime currentTime,
                                                                   List<PomodoroDto> reversedDailyPomodoros) {
        PeriodDto freeSlotBeforeOldestPomodoro = buildNearestPeriodBeforeOldestPomodoro(reversedDailyPomodoros);
        validatePeriod(freeSlotBeforeOldestPomodoro.getStart(), currentTime);

        return freeSlotBeforeOldestPomodoro;
    }

    private PeriodDto buildNearestPeriodBeforeOldestPomodoro(List<PomodoroDto> reversedDailyPomodoros) {
        PomodoroDto oldestDailyPomodoro = reversedDailyPomodoros.get(reversedDailyPomodoros.size() - 1);

        LocalDateTime periodStartTime = oldestDailyPomodoro.getStartTime()
                .toLocalDateTime()
                .minusMinutes(BREAK_BETWEEN_SLOTS)
                .minusMinutes(PomodoroChronoUtil.POMODORO_DURATION);

        LocalDateTime periodEndTime = oldestDailyPomodoro.getStartTime()
                .toLocalDateTime()
                .minusMinutes(BREAK_BETWEEN_SLOTS);

        return new PeriodDto(periodStartTime, periodEndTime);
    }

    private void validatePeriod(LocalDateTime periodStartTime, LocalDateTime currentTime) {
        LocalDateTime validStartTime = currentTime.toLocalDate()
                .atStartOfDay()
                .plusMinutes(BREAK_BETWEEN_SLOTS);

        if (periodStartTime.isBefore(validStartTime)) {
            throw new PomodoroException("Cannot save pomodoro automatically due to less than 20 minutes have " +
                    "passed since start of the day");
        }
    }

}

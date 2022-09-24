package com.igorgorbunov3333.timer.service.pomodoro.slot.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.service.exception.FreeSlotException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.slot.FreeSlotProviderService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultFreeSlotProviderService implements FreeSlotProviderService {

    private static final long BREAK_BETWEEN_SLOTS = 1L;

    private final CurrentTimeService currentTimeService;
    private final PomodoroEngine pomodoroEngine;

    @Override
    public PeriodDto findFreeSlotInCurrentDay(List<PeriodDto> reservedSlots) {  //TODO: validate that all slots from the same day
        LocalDateTime currentTime = currentTimeService.getCurrentDateTime();

        PeriodDto potentialFreeSlot = buildNearestFreeSlot(currentTime);
        validatePeriod(potentialFreeSlot.getStart(), currentTime);

        List<PeriodDto> reversedDailyReservedSlots = reversedDailyReservedSlots(reservedSlots);
        if (reversedDailyReservedSlots.isEmpty()) {
            return potentialFreeSlot;
        }

        return findNearestFreeSlot(currentTime, potentialFreeSlot, reversedDailyReservedSlots);
    }

    private PeriodDto buildNearestFreeSlot(LocalDateTime currentTime) {
        LocalDateTime currentPomodoroStartTime = pomodoroEngine.getCurrentPomodoroStartTime();
        if (currentPomodoroStartTime != null) {
            LocalDateTime periodEnd = currentPomodoroStartTime.minusMinutes(1L);
            LocalDateTime periodStart = periodEnd.minusMinutes(PomodoroChronoUtil.POMODORO_DURATION);

            PeriodDto freeSlot = new PeriodDto(periodStart, periodEnd);

            log.debug("Pomodoro is running currently. Nearest free slot is [{}]", freeSlot);

            return freeSlot;
        }

        LocalDateTime periodStart = currentTime.minusMinutes(BREAK_BETWEEN_SLOTS)
                .minusMinutes(PomodoroChronoUtil.POMODORO_DURATION);
        LocalDateTime periodEnd = periodStart.plusMinutes(PomodoroChronoUtil.POMODORO_DURATION);
        return new PeriodDto(periodStart, periodEnd);
    }

    private List<PeriodDto> reversedDailyReservedSlots(List<PeriodDto> dailyReservedSlots) {
        dailyReservedSlots.sort(Comparator.comparing(PeriodDto::getStart).reversed());

        return dailyReservedSlots;
    }

    private PeriodDto findNearestFreeSlot(LocalDateTime currentTime,
                                          PeriodDto nearestFreeSlot,
                                          List<PeriodDto> reversedDailyReservedSlots) {
        if (isPotentialFreeSlotStartAfterLatestReservedSlotEndPlusOneMinute(nearestFreeSlot.getStart(), reversedDailyReservedSlots)) {
            return nearestFreeSlot;
        }

        PeriodDto latestFreeSlotAtNearestValidGap = tryToFindLatestFreeSlotAtNearestValidGapBetweenReservedSlots(reversedDailyReservedSlots);

        return Objects.requireNonNullElseGet(
                latestFreeSlotAtNearestValidGap,
                () -> tryToFindFreeSlotBeforeOldestReservedSlotOrThrow(currentTime, reversedDailyReservedSlots)
        );
    }

    private boolean isPotentialFreeSlotStartAfterLatestReservedSlotEndPlusOneMinute(LocalDateTime potentialFreeSlotStartTime,
                                                                                    List<PeriodDto> reversedDailyReservedSlots) {
        LocalDateTime latestReservedSlotEndTime = reversedDailyReservedSlots.get(0)
                .getEnd();
        LocalDateTime oneMinuteAfterLatestReservedSlotEndTime = latestReservedSlotEndTime.plusMinutes(BREAK_BETWEEN_SLOTS);

        return potentialFreeSlotStartTime.isAfter(oneMinuteAfterLatestReservedSlotEndTime);
    }

    private PeriodDto tryToFindLatestFreeSlotAtNearestValidGapBetweenReservedSlots(List<PeriodDto> reversedDailyReservedSlots) {
        List<PeriodDto> gapsBetweenReservedSlots = findGapsBetweenReservedSlots(reversedDailyReservedSlots);

        return gapsBetweenReservedSlots.stream()
                .filter(this::isValidGap)
                .map(this::mapValidGapToPeriod)
                .findFirst()
                .orElse(null);
    }

    private List<PeriodDto> findGapsBetweenReservedSlots(List<PeriodDto> reservedSlots) {
        PeriodDto previousReservedSlot = null;
        LocalDateTime start;
        LocalDateTime end;
        List<PeriodDto> gaps = new LinkedList<>();

        for (PeriodDto currentReservedSlot : reservedSlots) {
            if (previousReservedSlot != null) {
                start = currentReservedSlot.getEnd();
                end = previousReservedSlot.getStart();
                gaps.add(new PeriodDto(start, end));
            }
            previousReservedSlot = currentReservedSlot;
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

    private PeriodDto tryToFindFreeSlotBeforeOldestReservedSlotOrThrow(LocalDateTime currentTime,
                                                                       List<PeriodDto> reversedDailyReservedSlots) {
        PeriodDto freeSlotBeforeOldestReservedSlot = buildNearestPeriodBeforeOldestReservedSlot(reversedDailyReservedSlots);
        validatePeriod(freeSlotBeforeOldestReservedSlot.getStart(), currentTime);

        return freeSlotBeforeOldestReservedSlot;
    }

    private PeriodDto buildNearestPeriodBeforeOldestReservedSlot(List<PeriodDto> reversedDailyReservedSlots) {
        PeriodDto oldestReservedSlot = reversedDailyReservedSlots.get(reversedDailyReservedSlots.size() - 1);

        LocalDateTime periodStartTime = oldestReservedSlot.getStart()
                .minusMinutes(BREAK_BETWEEN_SLOTS)
                .minusMinutes(PomodoroChronoUtil.POMODORO_DURATION);

        LocalDateTime periodEndTime = oldestReservedSlot.getStart()
                .minusMinutes(BREAK_BETWEEN_SLOTS);

        return new PeriodDto(periodStartTime, periodEndTime);
    }

    private void validatePeriod(LocalDateTime periodStartTime, LocalDateTime currentTime) {
        LocalDateTime validStartTime = currentTime.toLocalDate()
                .atStartOfDay()
                .plusMinutes(BREAK_BETWEEN_SLOTS);

        if (periodStartTime.isBefore(validStartTime)) {
            throw new FreeSlotException("Cannot save pomodoro automatically due to less than 20 minutes have " +
                    "passed since start of the day");
        }
    }

}

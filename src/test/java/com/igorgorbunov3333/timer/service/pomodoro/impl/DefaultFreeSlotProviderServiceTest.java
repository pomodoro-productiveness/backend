package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.service.exception.FreeSlotException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultFreeSlotProviderServiceTest {

    @InjectMocks
    private DefaultFreeSlotProviderService testee;

    @Mock
    private CurrentTimeService currentTimeService;
    @Mock
    private PomodoroEngine pomodoroEngine;

    @Test
    void find_WhenNoPomodorosInDay_ThenReturnNearestTimeWithEndingOneMinuteAgo() {
        LocalDateTime currentTime = LocalDateTime.of(2022, 5, 10, 22, 0, 0);
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentTime);

        PeriodDto actual = testee.findFreeSlotInCurrentDay(Collections.emptyList());

        assertThat(actual.getStart()).isEqualTo(currentTime.minusMinutes(1L).minusMinutes(20L));
        assertThat(actual.getEnd()).isEqualTo(currentTime.minusMinutes(1L));
    }

    @Test
    void find_WhenTodayOnlyOnePomodoroLessThanTwentyMinutesAgo_ThenReturnSlotWithEndingOneMinuteBeforeThisPomodoroStarted() {
        LocalDateTime currentTime = LocalDateTime.of(2022, 5, 10, 22, 0, 0);
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentTime);

        LocalDateTime pomodoroStartTime = currentTime.minusMinutes(30L);
        LocalDateTime pomodoroEndTime = currentTime.minusMinutes(10L);
        PeriodDto pomodoro = mockReservedSlot(pomodoroStartTime, pomodoroEndTime);

        PeriodDto actual = testee.findFreeSlotInCurrentDay(Collections.singletonList(pomodoro));

        assertThat(actual.getStart()).isEqualTo(pomodoroStartTime.minusMinutes(1L).minusMinutes(20L));
        assertThat(actual.getEnd()).isEqualTo(pomodoroStartTime.minusMinutes(1L));
    }

    @Test
    void find_WhenTodayMultiplePomodorosAnd20MinutesAgoWasPomodoroAndThereIsGapBetweenThemMoreThan20Minutes_ThenReturnNearestSlotToNowTimeBetweenThesePomodoros() {
        LocalDateTime currentTime = LocalDateTime.of(2022, 5, 10, 22, 0, 0);
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentTime);

        LocalDateTime firstPomodoroStartTime = currentTime.minusMinutes(30L);
        LocalDateTime firstPomodoroEndTime = firstPomodoroStartTime.plusMinutes(20L);
        PeriodDto firstPomodoro = mockReservedSlot(firstPomodoroStartTime, firstPomodoroEndTime);

        LocalDateTime secondPomodoroStartTime = firstPomodoroStartTime.minusMinutes(50L);
        LocalDateTime secondPomodoroEndTime = secondPomodoroStartTime.plusMinutes(20L);
        PeriodDto secondPomodoro = mockReservedSlot(secondPomodoroStartTime, secondPomodoroEndTime);

        LocalDateTime thirdPomodoroStartTime = secondPomodoroStartTime.minusMinutes(25L);
        LocalDateTime thirdPomodoroEndTime = thirdPomodoroStartTime.plusMinutes(20L);
        PeriodDto thirdPomodoro = mockReservedSlot(thirdPomodoroStartTime, thirdPomodoroEndTime);

        PeriodDto actual = testee.findFreeSlotInCurrentDay(Arrays.asList(firstPomodoro, secondPomodoro, thirdPomodoro));

        assertThat(actual.getStart()).isEqualTo(firstPomodoroStartTime.minusMinutes(1L).minusMinutes(20L));
        assertThat(actual.getEnd()).isEqualTo(firstPomodoroStartTime.minusMinutes(1L));
    }

    @Test
    void find_WhenTodayMultiplePomodorosAnd20MinutesAgoWasPomodoroAndNoGapBetween_ThenRetuenSlotBeforeFirstPomodoro() {
        LocalDateTime currentTime = LocalDateTime.of(2022, 5, 10, 22, 0, 0);
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentTime);

        LocalDateTime firstPomodoroStartTime = currentTime.minusMinutes(30L);
        LocalDateTime firstPomodoroEndTime = firstPomodoroStartTime.plusMinutes(20L);
        PeriodDto firstPomodoro = mockReservedSlot(firstPomodoroStartTime, firstPomodoroEndTime);

        LocalDateTime secondPomodoroStartTime = firstPomodoroStartTime.minusMinutes(25L);
        LocalDateTime secondPomodoroEndTime = secondPomodoroStartTime.plusMinutes(20L);
        PeriodDto secondPomodoro = mockReservedSlot(secondPomodoroStartTime, secondPomodoroEndTime);

        LocalDateTime thirdPomodoroStartTime = secondPomodoroStartTime.minusMinutes(25L);
        LocalDateTime thirdPomodoroEndTime = thirdPomodoroStartTime.plusMinutes(20L);
        PeriodDto thirdPomodoro = mockReservedSlot(thirdPomodoroStartTime, thirdPomodoroEndTime);

        PeriodDto actual = testee.findFreeSlotInCurrentDay(Arrays.asList(firstPomodoro, secondPomodoro, thirdPomodoro));

        assertThat(actual.getStart()).isEqualTo(thirdPomodoroStartTime.minusMinutes(1L).minusMinutes(20L));
        assertThat(actual.getEnd()).isEqualTo(thirdPomodoroStartTime.minusMinutes(1L));
    }

    @Test
    void find_WhenTodayNoPomodorosAndDayStartedLessThan20Minutes_ThenThrowException() {
        LocalDateTime currentTime = LocalDateTime.of(2022, 5, 10, 0, 10, 0);
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentTime);

        assertThatExceptionOfType(FreeSlotException.class)
                .isThrownBy(() -> testee.findFreeSlotInCurrentDay(List.of()));
    }

    @Test
    void find_WhenTodayMultiplePomodorosAndNoGapsBetweenAndPeriodBetweenDayStartsTimeAndFirstPomodoroStartLessThan20Minutes_ThenThrowException() {
        LocalDateTime currentTime = LocalDateTime.of(2022, 5, 10, 1, 10, 0);
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentTime);

        LocalDateTime firstPomodoroStartTime = currentTime.minusMinutes(30L);
        LocalDateTime firstPomodoroEndTime = firstPomodoroStartTime.plusMinutes(20L);
        PeriodDto firstPomodoro = mockReservedSlot(firstPomodoroStartTime, firstPomodoroEndTime);

        LocalDateTime secondPomodoroStartTime = firstPomodoroStartTime.minusMinutes(25L);
        LocalDateTime secondPomodoroEndTime = secondPomodoroStartTime.plusMinutes(20L);
        PeriodDto secondPomodoro = mockReservedSlot(secondPomodoroStartTime, secondPomodoroEndTime);

        assertThatExceptionOfType(FreeSlotException.class)
                 .isThrownBy(() -> testee.findFreeSlotInCurrentDay(Arrays.asList(firstPomodoro, secondPomodoro)));
    }

    private PeriodDto mockReservedSlot(LocalDateTime start, LocalDateTime end) {
        PeriodDto pomodoro = mock(PeriodDto.class);
        when(pomodoro.getStart()).thenReturn(start);
        when(pomodoro.getEnd()).thenReturn(end);

        return pomodoro;
    }

}

package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.WorkingPomodorosPerformanceRateDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.service.dayoff.LocalDayOffProvider;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkingTimeStandardCalculatorTest {

    private static final LocalDate WEEK_START_DAY = LocalDate.of(2022, 6, 6);
    private static final String POMODORO_WORK_TAG = "work";

    @InjectMocks
    private WorkingTimeStandardCalculator testee;

    @Mock
    private WeeklyLocalPomodoroProvider weeklyLocalPomodoroProvider;
    @Mock
    private PomodoroProperties pomodoroProperties;
    @Mock
    private LocalDayOffProvider localDayOffProvider;
    @Mock
    private CurrentTimeService currentTimeService;

    @Test
    void calculate_WhenEndOfWeekAndNoDayOffsAndNoWorkedPomodoro_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        mockCurrentTimeAtEndOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(-55);
    }

    @Test
    void calculate_WhenStartOfWeekAndNoDayOffsAndNoWorkedPomodoro_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        mockCurrentTimeAtStartOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(-11);
    }

    @Test
    void calculate_WhenMiddleOfWeekAndNoDayOffsAndNoWorkedPomodoro_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        mockCurrentTimeInMiddleOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(-33);
    }

    @Test
    void calculate_WhenEndOfWeekAndAllDaysAreDayOffsAndNoWorkedPomodoro_ThenCalculate() {
        mockLocalDayOffs(5);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(List.of());
        mockCurrentTimeAtEndOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(0);
    }

    @Test
    void calculate_WhenStartOfWeekAndAllDaysAreDayOffsAndNoWorkedPomodoro_ThenCalculate() {
        mockLocalDayOffs(5);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(List.of());
        mockCurrentTimeAtStartOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(0);
    }

    @Test
    void calculate_WhenMiddleOfWeekAndAllDaysAreDayOffsAndNoWorkedPomodoro_ThenCalculate() {
        mockLocalDayOffs(5);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(List.of());
        mockCurrentTimeInMiddleOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(0);
    }

    @Test
    void calculate_WhenEndOfWeekAndWorkedPomodoroMoreThanSetForStandard_ThenCalculate() {
        mockLocalDayOffs(1);
        List<PomodoroDto> pomodoros = mockPomodoros(61);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(pomodoros);
        mockCurrentTimeAtEndOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(17);
    }

    @Test
    void calculate_WhenStartOfWeekAndWorkedPomodoroMoreThanSetForStandard_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        List<PomodoroDto> pomodoros = mockPomodoros(13);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(pomodoros);
        mockCurrentTimeAtStartOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(2);
    }

    @Test
    void calculate_WhenMiddleOfWeekAndWorkedPomodoroMoreThanSetForStandard_ThenCalculate() {
        mockLocalDayOffs(1);
        List<PomodoroDto> pomodoros = mockPomodoros(35);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(pomodoros);
        mockCurrentTimeInMiddleOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(13);
    }

    @Test
    void calculate_WhenEndOfWeekAndWorkedPomodoroLessThanSetForStandard_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        List<PomodoroDto> pomodoros = mockPomodoros(50);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(pomodoros);
        mockCurrentTimeAtEndOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(-5);
    }

    @Test
    void calculate_WhenStartOfWeekAndWorkedPomodoroLessThanSetForStandard_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        List<PomodoroDto> pomodoros = mockPomodoros(8);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(pomodoros);
        mockCurrentTimeAtStartOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(-3);
    }

    @Test
    void calculate_WhenMiddleOfWeekAndWorkedPomodoroLessThanSetForStandard_ThenCalculate() {
        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(List.of());
        List<PomodoroDto> pomodoros = mockPomodoros(5);
        when(weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(POMODORO_WORK_TAG)).thenReturn(pomodoros);
        mockCurrentTimeInMiddleOfWeek();
        mockPomodoroProperties();

        WorkingPomodorosPerformanceRateDto actual = testee.calculate();

        assertThat(actual.getBalance()).isEqualTo(-28);
    }

    private void mockLocalDayOffs(long amountInWeek) {
        List<DayOff> dayOffs = new ArrayList<>();
        for (LocalDate startDate = WEEK_START_DAY; startDate.isBefore(WEEK_START_DAY.plusDays(amountInWeek)); startDate = startDate.plusDays(1L)) {
            DayOff dayOff = mock(DayOff.class);
            when(dayOff.getDay()).thenReturn(startDate);
            dayOffs.add(dayOff);
        }

        when(localDayOffProvider.provideCurrentWeekDayOffs()).thenReturn(dayOffs);
    }

    private List<PomodoroDto> mockPomodoros(int amount) {
        List<PomodoroDto> result = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            PomodoroDto pomodoroDto = mock(PomodoroDto.class);
            result.add(pomodoroDto);
        }
        return result;
    }

    private void mockCurrentTimeAtEndOfWeek() {
        when(currentTimeService.getCurrentDateTime()).thenReturn(WEEK_START_DAY.plusDays(6).atStartOfDay());
    }

    private void mockCurrentTimeAtStartOfWeek() {
        when(currentTimeService.getCurrentDateTime()).thenReturn(WEEK_START_DAY.atStartOfDay());
    }

    private void mockCurrentTimeInMiddleOfWeek() {
        when(currentTimeService.getCurrentDateTime()).thenReturn(WEEK_START_DAY.plusDays(2).atStartOfDay());
    }

    private void mockPomodoroProperties() {
        PomodoroProperties.Amount amount = mock(PomodoroProperties.Amount.class);
        when(amount.getWork()).thenReturn(11);

        when(pomodoroProperties.getAmount()).thenReturn(amount);

        PomodoroProperties.Tag tag = mock(PomodoroProperties.Tag.class);
        when(tag.getWork()).thenReturn("work");

        when(pomodoroProperties.getTag()).thenReturn(tag);
    }

}

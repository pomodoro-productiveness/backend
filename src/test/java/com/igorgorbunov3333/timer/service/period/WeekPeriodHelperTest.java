package com.igorgorbunov3333.timer.service.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeekPeriodHelperTest {

    @InjectMocks
    private WeekPeriodHelper testee;

    @Mock
    private CurrentTimeService currentTimeService;

    @ParameterizedTest
    @ValueSource(longs = {0L, 1, 2L, 3L, 4L, 5L, 6L})
    void getPreviousWeekPeriod_WhenMondayIsCurrentDay_ThenReturnPeriodOfPreviousWeek(long daysToAdd) {
        final LocalDate mondayDay = LocalDate.of(2022, 10, 10);
        when(currentTimeService.getCurrentDateTime())
                .thenReturn(mondayDay.atStartOfDay().plusDays(daysToAdd));

        PeriodDto actual = testee.providePreviousWeekPeriod();

        assertThat(actual.getStart()).isEqualTo(LocalDate.of(2022, 10, 3).atStartOfDay());
        assertThat(actual.getEnd()).isEqualTo(LocalDate.of(2022, 10, 9).atTime(LocalTime.MAX));
    }

}

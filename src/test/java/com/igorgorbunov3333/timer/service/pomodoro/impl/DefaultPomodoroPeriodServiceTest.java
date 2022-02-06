package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDtoV2;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.util.CurrentDayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPomodoroPeriodServiceTest {

    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private PomodoroMapper pomodoroMapper;
    @Mock
    private CurrentDayService currentDayService;

    @InjectMocks
    private DefaultPomodoroPeriodService testee;

    @Test
    void getCurrentWeekPomodoros_WhenNoWeeklyPomodoros_ThenReturnEmptyMap() {
        LocalDate currentDay = LocalDate.of(2022, 2, 5);
        when(currentDayService.getCurrentDay()).thenReturn(currentDay);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay();
        LocalDateTime end = currentDay.atTime(LocalTime.MAX);
        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(start, end)).thenReturn(List.of());

        Map<DayOfWeek, List<PomodoroDtoV2>> actual = testee.getCurrentWeekPomodoros();

        assertThat(actual).isEmpty();
    }

    @Test
    void getCurrentWeekPomodoros_WhenStartOfWeek_ThenReturnResult() {
        LocalDate currentDay = LocalDate.of(2022, 1, 31);
        when(currentDayService.getCurrentDay()).thenReturn(currentDay);
        LocalDateTime start = currentDay.atStartOfDay();
        LocalDateTime end = currentDay.atTime(LocalTime.MAX);

        Pomodoro firstPomodoro = mock(Pomodoro.class);
        when(firstPomodoro.getStartTime()).thenReturn(start.plusHours(3));
        Pomodoro secondPomodoro = mock(Pomodoro.class);
        when(secondPomodoro.getStartTime()).thenReturn(start.plusHours(4));
        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(start, end))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));
        PomodoroDtoV2 firstPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 secondPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        when(pomodoroMapper.mapToDto(List.of(firstPomodoro, secondPomodoro)))
                .thenReturn(List.of(firstPomodoroDtoV2, secondPomodoroDtoV2));

        Map<DayOfWeek, List<PomodoroDtoV2>> actual = testee.getCurrentWeekPomodoros();

        assertThat(actual)
                .containsExactlyEntriesOf(Map.of(DayOfWeek.MONDAY, List.of(firstPomodoroDtoV2, secondPomodoroDtoV2)));
    }

    @Test
    void getCurrentWeekPomodoros_WhenMiddleOfWeek_ThenReturnResult() {
        LocalDate currentDay = LocalDate.of(2022, 2, 2);
        when(currentDayService.getCurrentDay()).thenReturn(currentDay);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay();
        LocalDateTime end = currentDay.atTime(LocalTime.MAX);

        Pomodoro firstDayPomodoro = mock(Pomodoro.class);
        when(firstDayPomodoro.getStartTime()).thenReturn(start.plusHours(3));
        Pomodoro secondDayPomodoro = mock(Pomodoro.class);
        when(secondDayPomodoro.getStartTime()).thenReturn(start.plusDays(1).plusHours(4));
        Pomodoro thirdDayPomodoro = mock(Pomodoro.class);
        when(thirdDayPomodoro.getStartTime()).thenReturn(start.plusDays(2).plusHours(4));

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(start, end))
                .thenReturn(List.of(firstDayPomodoro, secondDayPomodoro, thirdDayPomodoro));
        PomodoroDtoV2 firstPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 secondPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 thirdPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        when(pomodoroMapper.mapToDto(List.of(firstDayPomodoro)))
                .thenReturn(List.of(firstPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(secondDayPomodoro)))
                .thenReturn(List.of(secondPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(thirdDayPomodoro)))
                .thenReturn(List.of(thirdPomodoroDtoV2));

        Map<DayOfWeek, List<PomodoroDtoV2>> actual = testee.getCurrentWeekPomodoros();

        Map<DayOfWeek, List<PomodoroDtoV2>> expected = Map.of(
                DayOfWeek.MONDAY, List.of(firstPomodoroDtoV2),
                DayOfWeek.TUESDAY, List.of(secondPomodoroDtoV2),
                DayOfWeek.WEDNESDAY, List.of(thirdPomodoroDtoV2));
        assertThat(actual)
                .containsExactlyEntriesOf(new TreeMap<>(expected));
    }

    @Test
    void getCurrentWeekPomodoros_WhenEndOfWeek_ThenReturnResult() {
        LocalDate currentDay = LocalDate.of(2022, 2, 6);
        when(currentDayService.getCurrentDay()).thenReturn(currentDay);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay();
        LocalDateTime end = currentDay.atTime(LocalTime.MAX);

        Pomodoro firstDayPomodoro = mock(Pomodoro.class);
        when(firstDayPomodoro.getStartTime()).thenReturn(start.plusHours(3));
        Pomodoro secondDayPomodoro = mock(Pomodoro.class);
        when(secondDayPomodoro.getStartTime()).thenReturn(start.plusDays(1).plusHours(4));
        Pomodoro thirdDayPomodoro = mock(Pomodoro.class);
        when(thirdDayPomodoro.getStartTime()).thenReturn(start.plusDays(2).plusHours(4));
        Pomodoro fourthDayPomodoro = mock(Pomodoro.class);
        when(fourthDayPomodoro.getStartTime()).thenReturn(start.plusDays(3).plusHours(4));
        Pomodoro fifthDayPomodoro = mock(Pomodoro.class);
        when(fifthDayPomodoro.getStartTime()).thenReturn(start.plusDays(4).plusHours(4));
        Pomodoro sixthDayPomodoro = mock(Pomodoro.class);
        when(sixthDayPomodoro.getStartTime()).thenReturn(start.plusDays(5).plusHours(4));
        Pomodoro seventhDayPomodoro = mock(Pomodoro.class);
        when(seventhDayPomodoro.getStartTime()).thenReturn(start.plusDays(6).plusHours(4));

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(start, end))
                .thenReturn(List.of(
                        firstDayPomodoro,
                        secondDayPomodoro,
                        thirdDayPomodoro,
                        fourthDayPomodoro,
                        fifthDayPomodoro,
                        sixthDayPomodoro,
                        seventhDayPomodoro
                ));
        PomodoroDtoV2 firstPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 secondPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 thirdPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 fourthPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 fifthPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 sixthPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        PomodoroDtoV2 seventhPomodoroDtoV2 = mock(PomodoroDtoV2.class);
        when(pomodoroMapper.mapToDto(List.of(firstDayPomodoro)))
                .thenReturn(List.of(firstPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(secondDayPomodoro)))
                .thenReturn(List.of(secondPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(thirdDayPomodoro)))
                .thenReturn(List.of(thirdPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(fourthDayPomodoro)))
                .thenReturn(List.of(fourthPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(fifthDayPomodoro)))
                .thenReturn(List.of(fifthPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(sixthDayPomodoro)))
                .thenReturn(List.of(sixthPomodoroDtoV2));
        when(pomodoroMapper.mapToDto(List.of(seventhDayPomodoro)))
                .thenReturn(List.of(seventhPomodoroDtoV2));

        Map<DayOfWeek, List<PomodoroDtoV2>> actual = testee.getCurrentWeekPomodoros();

        Map<DayOfWeek, List<PomodoroDtoV2>> expected = Map.of(
                DayOfWeek.MONDAY, List.of(firstPomodoroDtoV2),
                DayOfWeek.TUESDAY, List.of(secondPomodoroDtoV2),
                DayOfWeek.WEDNESDAY, List.of(thirdPomodoroDtoV2),
                DayOfWeek.THURSDAY, List.of(fourthPomodoroDtoV2),
                DayOfWeek.FRIDAY, List.of(fifthPomodoroDtoV2),
                DayOfWeek.SATURDAY, List.of(sixthPomodoroDtoV2),
                DayOfWeek.SUNDAY, List.of(seventhPomodoroDtoV2));
        assertThat(actual)
                .containsExactlyEntriesOf(new TreeMap<>(expected));
    }

}

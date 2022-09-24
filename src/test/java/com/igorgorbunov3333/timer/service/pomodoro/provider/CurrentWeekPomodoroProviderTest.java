package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentWeekPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentWeekPomodoroProviderTest {

    private static final ZoneId CURRENT_ZONE_ID = ZoneId.of("Europe/Kiev");

    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private PomodoroMapper pomodoroMapper;
    @Mock
    private CurrentTimeService currentTimeService;
    @Mock
    private DayOffRepository dayOffRepository;

    @InjectMocks
    private CurrentWeekPomodoroProvider testee;

    @Test
    void provideWeeklyPomodoro_WhenNoWeeklyPomodoro_ThenReturnEmptyMap() {
        LocalDateTime currentDayTime = LocalDate.of(2022, 2, 5).atStartOfDay(); //Saturday
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentDayTime);
        ZonedDateTime start = LocalDate.of(2022, 1, 31)
                .atStartOfDay()
                .atZone(CURRENT_ZONE_ID);
        ZonedDateTime end = currentDayTime.toLocalDate().atTime(LocalTime.MAX).atZone(CURRENT_ZONE_ID);
        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start, end)).thenReturn(List.of());

        WeeklyPomodoroDto actual = testee.provideWeeklyPomodoro();

        assertThat(actual.getPomodoro()).isEmpty();
    }

    @Test
    void provideWeeklyPomodoro_WhenStartOfWeek_ThenReturnResult() {
        LocalDateTime localDateTime = LocalDate.of(2022, 1, 31).atStartOfDay(); //Monday
        when(currentTimeService.getCurrentDateTime()).thenReturn(localDateTime);
        LocalDateTime start = localDateTime.toLocalDate().atStartOfDay();
        LocalDateTime end = localDateTime.toLocalDate().atTime(LocalTime.MAX);

        Pomodoro firstPomodoro = mock(Pomodoro.class);
        Pomodoro secondPomodoro = mock(Pomodoro.class);
        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start.atZone(CURRENT_ZONE_ID), end.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));
        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(start.plusHours(3).atZone(CURRENT_ZONE_ID));
        PomodoroDto secondPomodoroDto = mock(PomodoroDto.class);
        when(secondPomodoroDto.getStartTime()).thenReturn(start.plusHours(4).atZone(CURRENT_ZONE_ID));
        when(pomodoroMapper.mapToDto(firstPomodoro)).thenReturn(firstPomodoroDto);
        when(pomodoroMapper.mapToDto(secondPomodoro)).thenReturn(secondPomodoroDto);

        WeeklyPomodoroDto actual = testee.provideWeeklyPomodoro();

        assertThat(actual.getPomodoro())
                .extracting(dailyPomodoro -> Tuple.tuple(dailyPomodoro.getPomodoro(), dailyPomodoro.isDayOff(), dailyPomodoro.getDayOfWeek(), dailyPomodoro.getDate()))
                .containsExactlyElementsOf(List.of(Tuple.tuple(List.of(firstPomodoroDto, secondPomodoroDto), false, DayOfWeek.MONDAY, localDateTime.toLocalDate())));
    }

    @Test
    void provideWeeklyPomodoro_WhenMiddleOfWeek_ThenReturnResult() {
        LocalDateTime localDateTime = LocalDate.of(2022, 2, 2).atStartOfDay(); // Wednesday
        when(currentTimeService.getCurrentDateTime()).thenReturn(localDateTime);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay(); // Monday
        LocalDateTime end = localDateTime.toLocalDate().atTime(LocalTime.MAX);

        Pomodoro firstDayPomodoro = mock(Pomodoro.class);
        Pomodoro secondDayPomodoro = mock(Pomodoro.class);
        Pomodoro thirdDayPomodoro = mock(Pomodoro.class);

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start.atZone(CURRENT_ZONE_ID), end.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstDayPomodoro, secondDayPomodoro, thirdDayPomodoro));
        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(start.plusHours(3).atZone(CURRENT_ZONE_ID));
        PomodoroDto secondPomodoroDto = mock(PomodoroDto.class);
        when(secondPomodoroDto.getStartTime()).thenReturn(start.plusDays(1).plusHours(4).atZone(CURRENT_ZONE_ID));
        PomodoroDto thirdPomodoroDto = mock(PomodoroDto.class);
        when(thirdPomodoroDto.getStartTime()).thenReturn(start.plusDays(2).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(pomodoroMapper.mapToDto(firstDayPomodoro)).thenReturn(firstPomodoroDto);
        when(pomodoroMapper.mapToDto(secondDayPomodoro)).thenReturn(secondPomodoroDto);
        when(pomodoroMapper.mapToDto(thirdDayPomodoro)).thenReturn(thirdPomodoroDto);

        WeeklyPomodoroDto actual = testee.provideWeeklyPomodoro();

        assertThat(actual.getPomodoro())
                .extracting(dailyPomodoro -> Tuple.tuple(dailyPomodoro.getPomodoro(), dailyPomodoro.isDayOff(), dailyPomodoro.getDayOfWeek(), dailyPomodoro.getDate()))
                .containsExactlyElementsOf(List.of(
                        Tuple.tuple(List.of(firstPomodoroDto), false, DayOfWeek.MONDAY, start.toLocalDate()),
                        Tuple.tuple(List.of(secondPomodoroDto), false, DayOfWeek.TUESDAY, start.toLocalDate().plusDays(1L)),
                        Tuple.tuple(List.of(thirdPomodoroDto), false, DayOfWeek.WEDNESDAY, start.toLocalDate().plusDays(2L))
                ));
    }

    @Test
    void provideWeeklyPomodoro_WhenSomeDaysWithoutPomodoro_ThenReturnResult() {
        // Tuesday without pomodoro
        LocalDateTime localDateTime = LocalDate.of(2022, 2, 2).atStartOfDay(); // Wednesday
        when(currentTimeService.getCurrentDateTime()).thenReturn(localDateTime);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay(); // Monday
        LocalDateTime end = localDateTime.toLocalDate().atTime(LocalTime.MAX);

        Pomodoro firstDayPomodoro = mock(Pomodoro.class);
        Pomodoro thirdDayPomodoro = mock(Pomodoro.class);

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start.atZone(CURRENT_ZONE_ID), end.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstDayPomodoro, thirdDayPomodoro));
        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(start.plusHours(3).atZone(CURRENT_ZONE_ID));
        PomodoroDto thirdPomodoroDto = mock(PomodoroDto.class);
        when(thirdPomodoroDto.getStartTime()).thenReturn(start.plusDays(2).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(pomodoroMapper.mapToDto(firstDayPomodoro)).thenReturn(firstPomodoroDto);
        when(pomodoroMapper.mapToDto(thirdDayPomodoro)).thenReturn(thirdPomodoroDto);

        WeeklyPomodoroDto actual = testee.provideWeeklyPomodoro();

        assertThat(actual.getPomodoro())
                .extracting(dailyPomodoro -> Tuple.tuple(dailyPomodoro.getPomodoro(), dailyPomodoro.isDayOff(), dailyPomodoro.getDayOfWeek(), dailyPomodoro.getDate()))
                .containsExactlyElementsOf(List.of(
                        Tuple.tuple(List.of(firstPomodoroDto), false, DayOfWeek.MONDAY, start.toLocalDate()),
                        Tuple.tuple(List.of(), false, DayOfWeek.TUESDAY, start.toLocalDate().plusDays(1L)),
                        Tuple.tuple(List.of(thirdPomodoroDto), false, DayOfWeek.WEDNESDAY, start.toLocalDate().plusDays(2L))
                ));
    }

    @Test
    void provideWeeklyPomodoro_WhenEndOfWeek_ThenReturnResult() {
        LocalDateTime localDateTime = LocalDate.of(2022, 2, 6).atStartOfDay(); //Sunday
        when(currentTimeService.getCurrentDateTime()).thenReturn(localDateTime);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay(); //Monday
        LocalDateTime end = localDateTime.toLocalDate().atTime(LocalTime.MAX);

        Pomodoro firstDayPomodoro = mock(Pomodoro.class);
        Pomodoro secondDayPomodoro = mock(Pomodoro.class);
        Pomodoro thirdDayPomodoro = mock(Pomodoro.class);
        Pomodoro fourthDayPomodoro = mock(Pomodoro.class);
        Pomodoro fifthDayPomodoro = mock(Pomodoro.class);
        Pomodoro sixthDayPomodoro = mock(Pomodoro.class);
        Pomodoro seventhDayPomodoro = mock(Pomodoro.class);

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start.atZone(CURRENT_ZONE_ID), end.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(
                        firstDayPomodoro,
                        secondDayPomodoro,
                        thirdDayPomodoro,
                        fourthDayPomodoro,
                        fifthDayPomodoro,
                        sixthDayPomodoro,
                        seventhDayPomodoro
                ));
        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto secondPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto thirdPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto fourthPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto fifthPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto sixthPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto seventhPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(start.plusHours(3).atZone(CURRENT_ZONE_ID));
        when(secondPomodoroDto.getStartTime()).thenReturn(start.plusDays(1).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(thirdPomodoroDto.getStartTime()).thenReturn(start.plusDays(2).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(fourthPomodoroDto.getStartTime()).thenReturn(start.plusDays(3).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(fifthPomodoroDto.getStartTime()).thenReturn(start.plusDays(4).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(sixthPomodoroDto.getStartTime()).thenReturn(start.plusDays(5).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(seventhPomodoroDto.getStartTime()).thenReturn(start.plusDays(6).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(pomodoroMapper.mapToDto(firstDayPomodoro)).thenReturn(firstPomodoroDto);
        when(pomodoroMapper.mapToDto(secondDayPomodoro)).thenReturn(secondPomodoroDto);
        when(pomodoroMapper.mapToDto(thirdDayPomodoro)).thenReturn(thirdPomodoroDto);
        when(pomodoroMapper.mapToDto(fourthDayPomodoro)).thenReturn(fourthPomodoroDto);
        when(pomodoroMapper.mapToDto(fifthDayPomodoro)).thenReturn(fifthPomodoroDto);
        when(pomodoroMapper.mapToDto(sixthDayPomodoro)).thenReturn(sixthPomodoroDto);
        when(pomodoroMapper.mapToDto(seventhDayPomodoro)).thenReturn(seventhPomodoroDto);

        WeeklyPomodoroDto actual = testee.provideWeeklyPomodoro();

        assertThat(actual.getPomodoro())
                .extracting(dailyPomodoro -> Tuple.tuple(dailyPomodoro.getPomodoro(), dailyPomodoro.isDayOff(), dailyPomodoro.getDayOfWeek(), dailyPomodoro.getDate()))
                .containsExactlyElementsOf(List.of(
                        Tuple.tuple(List.of(firstPomodoroDto), false, DayOfWeek.MONDAY, start.toLocalDate()),
                        Tuple.tuple(List.of(secondPomodoroDto), false, DayOfWeek.TUESDAY, start.toLocalDate().plusDays(1L)),
                        Tuple.tuple(List.of(thirdPomodoroDto), false, DayOfWeek.WEDNESDAY, start.toLocalDate().plusDays(2L)),
                        Tuple.tuple(List.of(fourthPomodoroDto), false, DayOfWeek.THURSDAY, start.toLocalDate().plusDays(3L)),
                        Tuple.tuple(List.of(fifthPomodoroDto), false, DayOfWeek.FRIDAY, start.toLocalDate().plusDays(4L)),
                        Tuple.tuple(List.of(sixthPomodoroDto), false, DayOfWeek.SATURDAY, start.toLocalDate().plusDays(5L)),
                        Tuple.tuple(List.of(seventhPomodoroDto), false, DayOfWeek.SUNDAY, start.toLocalDate().plusDays(6L))
                ));
    }

    @Test
    void provideWeeklyPomodoro_WhenOneOfDayIsDayOff_ThenReturnMarkDayAsDayOffBySettingTrue() {
        LocalDateTime localDateTime = LocalDate.of(2022, 2, 6).atStartOfDay(); //Sunday
        when(currentTimeService.getCurrentDateTime()).thenReturn(localDateTime);
        LocalDateTime start = LocalDate.of(2022, 1, 31).atStartOfDay(); //Monday
        LocalDateTime end = localDateTime.toLocalDate().atTime(LocalTime.MAX);

        Pomodoro firstDayPomodoro = mock(Pomodoro.class);
        Pomodoro secondDayPomodoro = mock(Pomodoro.class);
        Pomodoro thirdDayPomodoro = mock(Pomodoro.class);
        Pomodoro fourthDayPomodoro = mock(Pomodoro.class);
        Pomodoro fifthDayPomodoro = mock(Pomodoro.class);
        Pomodoro sixthDayPomodoro = mock(Pomodoro.class);
        Pomodoro seventhDayPomodoro = mock(Pomodoro.class);

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start.atZone(CURRENT_ZONE_ID), end.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(
                        firstDayPomodoro,
                        secondDayPomodoro,
                        thirdDayPomodoro,
                        fourthDayPomodoro,
                        fifthDayPomodoro,
                        sixthDayPomodoro,
                        seventhDayPomodoro
                ));
        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto secondPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto thirdPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto fourthPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto fifthPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto sixthPomodoroDto = mock(PomodoroDto.class);
        PomodoroDto seventhPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(start.plusHours(3).atZone(CURRENT_ZONE_ID));
        when(secondPomodoroDto.getStartTime()).thenReturn(start.plusDays(1).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(thirdPomodoroDto.getStartTime()).thenReturn(start.plusDays(2).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(fourthPomodoroDto.getStartTime()).thenReturn(start.plusDays(3).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(fifthPomodoroDto.getStartTime()).thenReturn(start.plusDays(4).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(sixthPomodoroDto.getStartTime()).thenReturn(start.plusDays(5).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(seventhPomodoroDto.getStartTime()).thenReturn(start.plusDays(6).plusHours(4).atZone(CURRENT_ZONE_ID));
        when(pomodoroMapper.mapToDto(firstDayPomodoro)).thenReturn(firstPomodoroDto);
        when(pomodoroMapper.mapToDto(secondDayPomodoro)).thenReturn(secondPomodoroDto);
        when(pomodoroMapper.mapToDto(thirdDayPomodoro)).thenReturn(thirdPomodoroDto);
        when(pomodoroMapper.mapToDto(fourthDayPomodoro)).thenReturn(fourthPomodoroDto);
        when(pomodoroMapper.mapToDto(fifthDayPomodoro)).thenReturn(fifthPomodoroDto);
        when(pomodoroMapper.mapToDto(sixthDayPomodoro)).thenReturn(sixthPomodoroDto);
        when(pomodoroMapper.mapToDto(seventhDayPomodoro)).thenReturn(seventhPomodoroDto);

        DayOff dayOffMock = mock(DayOff.class);
        when(dayOffMock.getDay()).thenReturn(start.toLocalDate().plusDays(2L));

        when(dayOffRepository.findByDayGreaterThanEqualAndDayLessThanEqualOrderByDay(start.toLocalDate(), end.toLocalDate()))
                .thenReturn(List.of(dayOffMock));

        WeeklyPomodoroDto actual = testee.provideWeeklyPomodoro();

        assertThat(actual.getPomodoro())
                .extracting(dailyPomodoro -> Tuple.tuple(dailyPomodoro.getPomodoro(), dailyPomodoro.isDayOff(), dailyPomodoro.getDayOfWeek(), dailyPomodoro.getDate()))
                .containsExactlyElementsOf(List.of(
                        Tuple.tuple(List.of(firstPomodoroDto), false, DayOfWeek.MONDAY, start.toLocalDate()),
                        Tuple.tuple(List.of(secondPomodoroDto), false, DayOfWeek.TUESDAY, start.toLocalDate().plusDays(1L)),
                        Tuple.tuple(List.of(thirdPomodoroDto), true, DayOfWeek.WEDNESDAY, start.toLocalDate().plusDays(2L)),
                        Tuple.tuple(List.of(fourthPomodoroDto), false, DayOfWeek.THURSDAY, start.toLocalDate().plusDays(3L)),
                        Tuple.tuple(List.of(fifthPomodoroDto), false, DayOfWeek.FRIDAY, start.toLocalDate().plusDays(4L)),
                        Tuple.tuple(List.of(sixthPomodoroDto), false, DayOfWeek.SATURDAY, start.toLocalDate().plusDays(5L)),
                        Tuple.tuple(List.of(seventhPomodoroDto), false, DayOfWeek.SUNDAY, start.toLocalDate().plusDays(6L))
                ));
    }

}

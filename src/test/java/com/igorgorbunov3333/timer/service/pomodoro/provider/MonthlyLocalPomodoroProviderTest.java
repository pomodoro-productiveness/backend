package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthlyLocalPomodoroProviderTest {

    private static final ZoneId CURRENT_ZONE_ID = ZoneId.of("Europe/Kiev");

    @Mock
    private CurrentTimeService currentTimeService;
    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private PomodoroMapper pomodoroMapper;

    @InjectMocks
    private MonthlyLocalPomodoroProvider testee;

    @Captor
    ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor;

    @Test
    void provide_WhenPomodoroWithChildTagToProvidedTag_ThenProvide() {
        String childTagName = "childTag";
        String parenTagName = "parentTag";

        mockPomodoroWithTags(childTagName, parenTagName);

        PomodoroDto pomodoroDtoMock = mock(PomodoroDto.class);
        when(pomodoroDtoMock.getStartTime()).thenReturn(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1).atZone(ZoneId.systemDefault()));
        when(pomodoroMapper.mapToDto(pomodoroArgumentCaptor.capture())).thenReturn(pomodoroDtoMock);

        testee.provide(parenTagName);

        List<Pomodoro> actualPomodoros = pomodoroArgumentCaptor.getAllValues();
        List<String> pomodoroTags = actualPomodoros.stream().map(p -> p.getTag().getName()).collect(Collectors.toList());
        assertThat(pomodoroTags).containsExactlyElementsOf(List.of(childTagName, childTagName));
    }

    @Test
    void provide_WhenPomodoroWithParentTagToProvidedTag_ThenProvide() {
        String parenTagName = "parentTag";

        mockPomodoroWithTags(null, parenTagName);

        PomodoroDto pomodoroDtoMock = mock(PomodoroDto.class);
        when(pomodoroDtoMock.getStartTime()).thenReturn(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1).atZone(ZoneId.systemDefault()));
        when(pomodoroMapper.mapToDto(pomodoroArgumentCaptor.capture())).thenReturn(pomodoroDtoMock);

        testee.provide(parenTagName);

        List<Pomodoro> actualPomodoros = pomodoroArgumentCaptor.getAllValues();
        List<String> pomodoroTags = actualPomodoros.stream().map(p -> p.getTag().getName()).collect(Collectors.toList());
        assertThat(pomodoroTags).containsExactlyElementsOf(List.of(parenTagName, parenTagName));
    }

    private void mockPomodoroWithTags(String childTagName, String parentTagName) {
        LocalDateTime currentDayTime = LocalDate.of(2022, 2, 5).atStartOfDay(); //Saturday
        when(currentTimeService.getCurrentDateTime()).thenReturn(currentDayTime);
        ZonedDateTime start = LocalDate.of(2022, 2, 1)
                .atStartOfDay()
                .atZone(CURRENT_ZONE_ID);
        ZonedDateTime end = currentDayTime.toLocalDate().atTime(LocalTime.MAX).atZone(CURRENT_ZONE_ID);

        PomodoroTag parentTag = mock(PomodoroTag.class);
        when(parentTag.getName()).thenReturn(parentTagName);

        PomodoroTag childTag = null;
        if (childTagName != null) {
            childTag = mock(PomodoroTag.class);
            when(childTag.getName()).thenReturn(childTagName);
            when(childTag.getParent()).thenReturn(parentTag);
        }

        Pomodoro firsPomodoro = mock(Pomodoro.class);
        when(firsPomodoro.getTag()).thenReturn(childTag == null ? parentTag : childTag);
        Pomodoro secondPomodoro = mock(Pomodoro.class);
        when(secondPomodoro.getTag()).thenReturn(childTag == null ? parentTag : childTag);
        List<Pomodoro> pomodoroList = List.of(firsPomodoro, secondPomodoro);

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(start, end)).thenReturn(pomodoroList);
    }

}

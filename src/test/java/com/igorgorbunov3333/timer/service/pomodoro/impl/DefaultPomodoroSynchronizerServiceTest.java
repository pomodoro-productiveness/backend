package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPomodoroSynchronizerServiceTest {

    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private GoogleDriveService googleDriveService;

    @InjectMocks
    private DefaultPomodoroSynchronizerService testee;

    @Test
    void synchronize_WhenRemotePomodorosSameAsLocal_ThenDoNotSynchronize() {
        final LocalDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0);
        final LocalDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20);
        final LocalDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0);
        final LocalDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20);

        Pomodoro firstPomodoro = mock(Pomodoro.class);
        when(firstPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstPomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        Pomodoro secondPomodoro = mock(Pomodoro.class);
        when(secondPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondPomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        when(pomodoroRepository.findAll()).thenReturn(List.of(firstPomodoro, secondPomodoro));

        PomodoroDto firstRemotePomodoro = mock(PomodoroDto.class);
        when(firstRemotePomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstRemotePomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondRemotePomodoro = mock(PomodoroDto.class);
        when(secondRemotePomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondRemotePomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(firstRemotePomodoro, secondRemotePomodoro));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);

        testee.synchronize();

        verifyNoMoreInteractions(pomodoroRepository);
        verifyNoMoreInteractions(googleDriveService);
    }

}

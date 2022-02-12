package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPomodoroSynchronizerServiceTest {

    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private GoogleDriveService googleDriveService;

    @Captor
    private ArgumentCaptor<List<Pomodoro>> pomodorosArgumentCaptor;
    @Captor
    private ArgumentCaptor<PomodoroDataDto> pomodoroDataArgumentCaptor;

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

    @Test
    void synchronize_WhenNoRemoteAndLocalPomodoros_ThenDoNotSynchronize() {
        when(pomodoroRepository.findAll()).thenReturn(List.of());
        when(googleDriveService.getPomodoroData()).thenReturn(mock(PomodoroDataDto.class));

        testee.synchronize();

        verifyNoMoreInteractions(pomodoroRepository);
        verifyNoMoreInteractions(googleDriveService);
    }

    @Test
    void synchronize_WhenOnlyRemotePomodorosPresent_ThenUpdateLocalPomodoros() {
        final LocalDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0);
        final LocalDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20);
        final LocalDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0);
        final LocalDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20);

        when(pomodoroRepository.findAll()).thenReturn(List.of());

        PomodoroDto firstRemotePomodoro = mock(PomodoroDto.class);
        when(firstRemotePomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstRemotePomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondRemotePomodoro = mock(PomodoroDto.class);
        when(secondRemotePomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondRemotePomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(firstRemotePomodoro, secondRemotePomodoro));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        when(pomodoroRepository.saveAll(pomodorosArgumentCaptor.capture())).thenReturn(List.of());

        testee.synchronize();

        verifyNoMoreInteractions(googleDriveService);
        List<Pomodoro> actualPomodorosToSave = pomodorosArgumentCaptor.getValue();
        assertThat(actualPomodorosToSave)
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime)
                ));
    }

    @Test
    void synchronize_WhenOnlyLocalPomodorosPresent_ThenUpdateRemotePomodoros() {
        final LocalDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0);
        final LocalDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20);
        final LocalDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0);
        final LocalDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20);


        Pomodoro firstLocalPomodoro = mock(Pomodoro.class);
        when(firstLocalPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstLocalPomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        Pomodoro secondLocalPomodoro = mock(Pomodoro.class);
        when(secondLocalPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondLocalPomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        when(pomodoroRepository.findAll()).thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of());
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        doNothing().when(googleDriveService).updatePomodoroData(pomodoroDataArgumentCaptor.capture());

        testee.synchronize();

        verifyNoMoreInteractions(pomodoroRepository);
        PomodoroDataDto actualPomodoroData = pomodoroDataArgumentCaptor.getValue();
        assertThat(actualPomodoroData.getPomodoros())
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime)
                ));
    }

    @Test
    void synchronize_WhenRemotePomodorosDifferFromLocal_ThenUpdateLocalPomodoros() {

    }

    @Test
    void synchronize_WhenLocalPomodorosDifferFromRemote_ThenUpdateRemotePomodoros() {

    }

}

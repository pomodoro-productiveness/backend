package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroInfoSynchronizationService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.impl.DefaultPomodoroSynchronizerService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPomodoroSynchronizerServiceTest {

    private static final LocalDateTime SYNCHRONIZATION_BOUND_TIMESTAMP =
            LocalDateTime.of(2022, 3, 5, 1, 1);

    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private GoogleDriveService googleDriveService;
    @Mock
    private PomodoroInfoSynchronizationService pomodoroInfoSynchronizationService;

    @Captor
    private ArgumentCaptor<List<Pomodoro>> localPomodorosArgumentCaptor;
    @Captor
    private ArgumentCaptor<PomodoroDataDto> remmotePomodoroDataArgumentCaptor;

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
        when(pomodoroRepository.findByEndTimeOrEndTimeBefore(SYNCHRONIZATION_BOUND_TIMESTAMP))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));

        PomodoroDto firstRemotePomodoro = mock(PomodoroDto.class);
        when(firstRemotePomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstRemotePomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondRemotePomodoro = mock(PomodoroDto.class);
        when(secondRemotePomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondRemotePomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(firstRemotePomodoro, secondRemotePomodoro));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        verifyNoMoreInteractions(googleDriveService);
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_NOTHING, null);
    }

    @Test
    void synchronize_WhenNoRemoteAndLocalPomodoros_ThenDoNotSynchronize() {
        when(pomodoroRepository.findByEndTimeOrEndTimeBefore(SYNCHRONIZATION_BOUND_TIMESTAMP))
                .thenReturn(List.of());
        when(googleDriveService.getPomodoroData()).thenReturn(mock(PomodoroDataDto.class));

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        verifyNoMoreInteractions(googleDriveService);
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_NOTHING, null);
    }

    @Test
    void synchronize_WhenOnlyRemotePomodorosPresent_ThenUpdateLocalPomodoros() {
        final LocalDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0);
        final LocalDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20);
        final LocalDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0);
        final LocalDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20);

        when(pomodoroRepository.findByEndTimeOrEndTimeBefore(SYNCHRONIZATION_BOUND_TIMESTAMP))
                .thenReturn(List.of());

        PomodoroDto firstRemotePomodoro = mock(PomodoroDto.class);
        when(firstRemotePomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstRemotePomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondRemotePomodoro = mock(PomodoroDto.class);
        when(secondRemotePomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondRemotePomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(firstRemotePomodoro, secondRemotePomodoro));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        when(pomodoroRepository.saveAll(localPomodorosArgumentCaptor.capture())).thenReturn(List.of());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(googleDriveService);
        List<Pomodoro> actualPomodorosToSave = localPomodorosArgumentCaptor.getValue();
        assertThat(actualPomodorosToSave)
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_LOCALLY, null);
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
        when(pomodoroRepository.findByEndTimeOrEndTimeBefore(SYNCHRONIZATION_BOUND_TIMESTAMP))
                .thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of());
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        doNothing().when(googleDriveService).updatePomodoroData(remmotePomodoroDataArgumentCaptor.capture());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        PomodoroDataDto actualPomodoroData = remmotePomodoroDataArgumentCaptor.getValue();
        assertThat(actualPomodoroData.getPomodoros())
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_REMOTELY, null);
    }

    @Test
    void synchronize_WhenRemotePomodorosDifferFromLocal_ThenUpdateLocalPomodoros() {
        final LocalDateTime previousPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0);
        final LocalDateTime previousPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20);
        final LocalDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 2, 1, 7, 0);
        final LocalDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 2, 1, 7, 20);
        final LocalDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 2, 2, 7, 0);
        final LocalDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 2, 2, 7, 20);
        final LocalDateTime nextPomodoroStartTime = LocalDateTime.of(2022, 3, 1, 7, 0);
        final LocalDateTime nextPomodoroEndTime = LocalDateTime.of(2022, 3, 1, 7, 20);

        Pomodoro firstLocalPomodoro = mock(Pomodoro.class);
        when(firstLocalPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstLocalPomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        Pomodoro secondLocalPomodoro = mock(Pomodoro.class);
        when(secondLocalPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondLocalPomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        when(pomodoroRepository.findByEndTimeOrEndTimeBefore(SYNCHRONIZATION_BOUND_TIMESTAMP))
                .thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));

        PomodoroDto previousPomodoro = mock(PomodoroDto.class);
        when(previousPomodoro.getStartTime()).thenReturn(previousPomodoroStartTime);
        when(previousPomodoro.getEndTime()).thenReturn(previousPomodoroEndTime);
        PomodoroDto firstRemotePomodoro = mock(PomodoroDto.class);
        when(firstRemotePomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstRemotePomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondRemotePomodoro = mock(PomodoroDto.class);
        when(secondRemotePomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondRemotePomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        PomodoroDto nextPomodoro = mock(PomodoroDto.class);
        when(nextPomodoro.getStartTime()).thenReturn(nextPomodoroStartTime);
        when(nextPomodoro.getEndTime()).thenReturn(nextPomodoroEndTime);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(
                previousPomodoro,
                firstRemotePomodoro,
                secondRemotePomodoro,
                nextPomodoro
        ));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        when(pomodoroRepository.saveAll(localPomodorosArgumentCaptor.capture())).thenReturn(List.of());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(googleDriveService);
        List<Pomodoro> actualPomodoroToSaveLocally = localPomodorosArgumentCaptor.getValue();
        assertThat(actualPomodoroToSaveLocally)
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(previousPomodoroStartTime, previousPomodoroEndTime),
                        Tuple.tuple(nextPomodoroStartTime, nextPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_LOCALLY, null);
    }

    @Test
    void synchronize_WhenLocalPomodorosDifferFromRemote_ThenUpdateRemotePomodoros() {
        final LocalDateTime previousPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0);
        final LocalDateTime previousPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20);
        final LocalDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 2, 1, 7, 0);
        final LocalDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 2, 1, 7, 20);
        final LocalDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 2, 2, 7, 0);
        final LocalDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 2, 2, 7, 20);
        final LocalDateTime nextPomodoroStartTime = LocalDateTime.of(2022, 3, 1, 7, 0);
        final LocalDateTime nextPomodoroEndTime = LocalDateTime.of(2022, 3, 1, 7, 20);

        Pomodoro previousLocalPomodoro = mock(Pomodoro.class);
        when(previousLocalPomodoro.getStartTime()).thenReturn(previousPomodoroStartTime);
        when(previousLocalPomodoro.getEndTime()).thenReturn(previousPomodoroEndTime);
        Pomodoro firstLocalPomodoro = mock(Pomodoro.class);
        when(firstLocalPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstLocalPomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        Pomodoro secondLocalPomodoro = mock(Pomodoro.class);
        when(secondLocalPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondLocalPomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        Pomodoro nextLocalPomodoro = mock(Pomodoro.class);
        when(nextLocalPomodoro.getStartTime()).thenReturn(nextPomodoroStartTime);
        when(nextLocalPomodoro.getEndTime()).thenReturn(nextPomodoroEndTime);
        when(pomodoroRepository.findByEndTimeOrEndTimeBefore(SYNCHRONIZATION_BOUND_TIMESTAMP))
                .thenReturn(List.of(
                        previousLocalPomodoro,
                        firstLocalPomodoro,
                        secondLocalPomodoro,
                        nextLocalPomodoro
                ));

        PomodoroDto firstRemotePomodoro = mock(PomodoroDto.class);
        when(firstRemotePomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstRemotePomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondRemotePomodoro = mock(PomodoroDto.class);
        when(secondRemotePomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondRemotePomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(
                firstRemotePomodoro,
                secondRemotePomodoro
        ));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        doNothing().when(googleDriveService).updatePomodoroData(remmotePomodoroDataArgumentCaptor.capture());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        PomodoroDataDto actualRemotePomodoroData = remmotePomodoroDataArgumentCaptor.getValue();
        assertThat(actualRemotePomodoroData.getPomodoros())
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(previousPomodoroStartTime, previousPomodoroEndTime),
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime),
                        Tuple.tuple(nextPomodoroStartTime, nextPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_REMOTELY, null);
    }

}

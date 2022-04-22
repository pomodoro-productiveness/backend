package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPomodoroSynchronizerServiceTest {

    private static final ZoneId CURRENT_ZONE_ID = ZoneId.of("Europe/Kiev");
    private static final LocalDateTime SYNCHRONIZATION_BOUND_TIMESTAMP =
            LocalDateTime.of(2022, 3, 5, 1, 1);

    @Mock
    private PomodoroRepository pomodoroRepository;
    @Mock
    private GoogleDriveService googleDriveService;
    @Mock
    private PomodoroInfoSynchronizationService pomodoroInfoSynchronizationService;
    @Mock
    private PomodoroMapper pomodoroMapper;

    @Captor
    private ArgumentCaptor<List<Pomodoro>> localPomodorosArgumentCaptor;
    @Captor
    private ArgumentCaptor<PomodoroDataDto> remmotePomodoroDataArgumentCaptor;

    @InjectMocks
    private DefaultPomodoroSynchronizerService testee;

    @Test
    void synchronize_WhenRemotePomodorosSameAsLocal_ThenDoNotSynchronize() {
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0)
                .atZone(ZoneId.systemDefault());

        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(firstPomodoroStartTime);
        PomodoroDto secondPomodoroDto = mock(PomodoroDto.class);
        when(secondPomodoroDto.getStartTime()).thenReturn(secondPomodoroStartTime);
        Pomodoro firstPomodoro = mock(Pomodoro.class);
        when(firstPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        Pomodoro secondPomodoro = mock(Pomodoro.class);
        when(secondPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));
        when(pomodoroMapper.mapToDto(List.of(firstPomodoro, secondPomodoro)))
                .thenReturn(List.of(firstPomodoroDto, secondPomodoroDto));

        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(firstPomodoroDto, secondPomodoroDto));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        verifyNoMoreInteractions(googleDriveService);
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_NOTHING, null);
    }

    @Test
    void synchronize_WhenNoRemoteAndLocalPomodoros_ThenDoNotSynchronize() {
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of());
        when(googleDriveService.getPomodoroData()).thenReturn(mock(PomodoroDataDto.class));

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        verifyNoMoreInteractions(googleDriveService);
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_NOTHING, null);
    }

    @Test
    void synchronize_WhenOnlyRemotePomodorosPresent_ThenUpdateLocalPomodoros() {
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20)
                .atZone(ZoneId.systemDefault());

        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
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
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20)
                .atZone(ZoneId.systemDefault());

        PomodoroDto firstPomodoro = mock(PomodoroDto.class);
        when(firstPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstPomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondPomodoro = mock(PomodoroDto.class);
        when(secondPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondPomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        Pomodoro firstLocalPomodoro = mock(Pomodoro.class);
        when(firstLocalPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        Pomodoro secondLocalPomodoro = mock(Pomodoro.class);
        when(secondLocalPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));
        when(pomodoroMapper.mapToDto(List.of(firstLocalPomodoro, secondLocalPomodoro)))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));

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
        final ZonedDateTime previousPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime previousPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneId.systemDefault());;
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 2, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 2, 1, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 2, 2, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 2, 2, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime nextPomodoroStartTime = LocalDateTime.of(2022, 3, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime nextPomodoroEndTime = LocalDateTime.of(2022, 3, 1, 7, 20)
                .atZone(ZoneId.systemDefault());

        PomodoroDto firstLocalPomodoro = mock(PomodoroDto.class);
        when(firstLocalPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(firstLocalPomodoro.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto secondLocalPomodoro = mock(PomodoroDto.class);
        when(secondLocalPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(secondLocalPomodoro.getEndTime()).thenReturn(secondPomodoroEndTime);
        Pomodoro firstPomodoro = mock(Pomodoro.class);
        when(firstPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        Pomodoro secondPomodoro = mock(Pomodoro.class);
        when(secondPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));
        when(pomodoroMapper.mapToDto(List.of(firstPomodoro, secondPomodoro)))
                .thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));

        PomodoroDto previousPomodoro = mock(PomodoroDto.class);
        when(previousPomodoro.getStartTime()).thenReturn(previousPomodoroStartTime);
        when(previousPomodoro.getEndTime()).thenReturn(previousPomodoroEndTime);
        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        PomodoroDto nextPomodoro = mock(PomodoroDto.class);
        when(nextPomodoro.getStartTime()).thenReturn(nextPomodoroStartTime);
        when(nextPomodoro.getEndTime()).thenReturn(nextPomodoroEndTime);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(
                previousPomodoro,
                firstLocalPomodoro,
                secondLocalPomodoro,
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
        final ZonedDateTime previousPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime previousPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 2, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 2, 1, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 2, 2, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 2, 2, 7, 20)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime nextPomodoroStartTime = LocalDateTime.of(2022, 3, 1, 7, 0)
                .atZone(ZoneId.systemDefault());
        final ZonedDateTime nextPomodoroEndTime = LocalDateTime.of(2022, 3, 1, 7, 20)
                .atZone(ZoneId.systemDefault());

        PomodoroDto firstPomodoroDto = mock(PomodoroDto.class);
        when(firstPomodoroDto.getStartTime()).thenReturn(previousPomodoroStartTime);
        when(firstPomodoroDto.getEndTime()).thenReturn(previousPomodoroEndTime);
        PomodoroDto secondPomodoroDto = mock(PomodoroDto.class);
        when(secondPomodoroDto.getStartTime()).thenReturn(firstPomodoroStartTime);
        when(secondPomodoroDto.getEndTime()).thenReturn(firstPomodoroEndTime);
        PomodoroDto thirdPomodoroDto = mock(PomodoroDto.class);
        when(thirdPomodoroDto.getStartTime()).thenReturn(secondPomodoroStartTime);
        when(thirdPomodoroDto.getEndTime()).thenReturn(secondPomodoroEndTime);
        PomodoroDto fourthPomodoroDto = mock(PomodoroDto.class);
        when(fourthPomodoroDto.getStartTime()).thenReturn(nextPomodoroStartTime);
        when(fourthPomodoroDto.getEndTime()).thenReturn(nextPomodoroEndTime);

        Pomodoro previousLocalPomodoro = mock(Pomodoro.class);
        when(previousLocalPomodoro.getStartTime()).thenReturn(previousPomodoroStartTime);
        Pomodoro firstLocalPomodoro = mock(Pomodoro.class);
        when(firstLocalPomodoro.getStartTime()).thenReturn(firstPomodoroStartTime);
        Pomodoro secondLocalPomodoro = mock(Pomodoro.class);
        when(secondLocalPomodoro.getStartTime()).thenReturn(secondPomodoroStartTime);
        Pomodoro nextLocalPomodoro = mock(Pomodoro.class);
        when(nextLocalPomodoro.getStartTime()).thenReturn(nextPomodoroStartTime);
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(
                        previousLocalPomodoro,
                        firstLocalPomodoro,
                        secondLocalPomodoro,
                        nextLocalPomodoro
                ));
        when(pomodoroMapper.mapToDto(List.of(
                previousLocalPomodoro,
                firstLocalPomodoro,
                secondLocalPomodoro,
                nextLocalPomodoro
        ))).thenReturn(List.of(
                firstPomodoroDto,
                secondPomodoroDto,
                thirdPomodoroDto,
                fourthPomodoroDto
        ));

        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of(
                secondPomodoroDto,
                thirdPomodoroDto
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

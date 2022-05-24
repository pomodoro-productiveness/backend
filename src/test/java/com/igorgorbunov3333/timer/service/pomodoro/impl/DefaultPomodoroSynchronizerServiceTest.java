package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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
    private ArgumentCaptor<PomodoroDataDto> remotePomodoroDataArgumentCaptor;

    @InjectMocks
    private DefaultPomodoroSynchronizerService testee;

    @Test
    void synchronize_WhenRemotePomodorosSameAsLocal_ThenDoNotSynchronize() {
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0)
                .atZone(ZoneOffset.UTC);

        PomodoroDto firstPomodoroDto = new PomodoroDto(null, firstPomodoroStartTime, firstPomodoroStartTime.plusMinutes(20L), false, List.of());
        PomodoroDto secondPomodoroDto = new PomodoroDto(null, secondPomodoroStartTime, secondPomodoroStartTime.plusMinutes(20L), false, List.of());
        Pomodoro firstPomodoro = new Pomodoro(null, firstPomodoroStartTime, firstPomodoroStartTime.plusMinutes(20L), false, List.of());
        Pomodoro secondPomodoro = new Pomodoro(null, secondPomodoroStartTime, secondPomodoroStartTime.plusMinutes(20L), false, List.of());
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
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 1, 2, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 1, 2, 7, 20)
                .atZone(ZoneOffset.UTC);

        PomodoroDto firstPomodoro = new PomodoroDto(null, firstPomodoroStartTime, firstPomodoroEndTime, false, List.of());
        PomodoroDto secondPomodoro = new PomodoroDto(null, secondPomodoroStartTime, secondPomodoroEndTime, false, List.of());
        Pomodoro firstLocalPomodoro = new Pomodoro(null, firstPomodoroStartTime, firstPomodoroEndTime, false, List.of());
        Pomodoro secondLocalPomodoro = new Pomodoro(null, secondPomodoroStartTime, secondPomodoroEndTime, false, List.of());
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));
        when(pomodoroMapper.mapToDto(List.of(firstLocalPomodoro, secondLocalPomodoro)))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));

        PomodoroDataDto pomodoroData = mock(PomodoroDataDto.class);
        when(pomodoroData.getPomodoros()).thenReturn(List.of());
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        doNothing().when(googleDriveService).updatePomodoroData(remotePomodoroDataArgumentCaptor.capture());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        PomodoroDataDto actualPomodoroData = remotePomodoroDataArgumentCaptor.getValue();
        assertThat(actualPomodoroData.getPomodoros())
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime().withZoneSameInstant(ZoneOffset.UTC),
                        pomodoro.getEndTime().withZoneSameInstant(ZoneOffset.UTC)))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_REMOTELY, null);
    }

    @Test
    void synchronize_WhenRemotePomodorosDifferFromLocal_ThenUpdateLocalPomodoros() {
        final ZonedDateTime previousPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime previousPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 2, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 2, 1, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 2, 2, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 2, 2, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime nextPomodoroStartTime = LocalDateTime.of(2022, 3, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime nextPomodoroEndTime = LocalDateTime.of(2022, 3, 1, 7, 20)
                .atZone(ZoneOffset.UTC);

        PomodoroDto firstLocalPomodoro = new PomodoroDto(null, firstPomodoroStartTime, firstPomodoroEndTime, false, List.of());
        PomodoroDto secondLocalPomodoro = new PomodoroDto(null, secondPomodoroStartTime, secondPomodoroEndTime, false, List.of());
        Pomodoro firstPomodoro = new Pomodoro(null, firstPomodoroStartTime, firstPomodoroStartTime.plusMinutes(20L), false, List.of());
        Pomodoro secondPomodoro = new Pomodoro(null, secondPomodoroStartTime, secondPomodoroStartTime.plusMinutes(20L), false, List.of());
        when(pomodoroRepository.findByEndTimeLessThanEqual(SYNCHRONIZATION_BOUND_TIMESTAMP.atZone(CURRENT_ZONE_ID)))
                .thenReturn(List.of(firstPomodoro, secondPomodoro));
        when(pomodoroMapper.mapToDto(anyList()))
                .thenReturn(List.of(firstLocalPomodoro, secondLocalPomodoro));

        PomodoroDto previousPomodoro = new PomodoroDto(null, previousPomodoroStartTime, previousPomodoroEndTime, false, List.of());
        PomodoroDto nextPomodoro = new PomodoroDto(null, nextPomodoroStartTime, nextPomodoroEndTime, false, List.of());
        PomodoroDataDto pomodoroData = new PomodoroDataDto(List.of(previousPomodoro, firstLocalPomodoro, secondLocalPomodoro, nextPomodoro));
        when(googleDriveService.getPomodoroData()).thenReturn(pomodoroData);
        when(pomodoroRepository.saveAll(localPomodorosArgumentCaptor.capture())).thenReturn(List.of());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(googleDriveService);
        List<Pomodoro> actualPomodoroToSaveLocally = localPomodorosArgumentCaptor.getValue();
        assertThat(actualPomodoroToSaveLocally)
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime().withZoneSameInstant(ZoneOffset.UTC),
                        pomodoro.getEndTime().withZoneSameInstant(ZoneOffset.UTC)))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(previousPomodoroStartTime, previousPomodoroEndTime),
                        Tuple.tuple(nextPomodoroStartTime, nextPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_LOCALLY, null);
    }

    @Test
    void synchronize_WhenLocalPomodorosDifferFromRemote_ThenUpdateRemotePomodoros() {
        final ZonedDateTime previousPomodoroStartTime = LocalDateTime.of(2022, 1, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime previousPomodoroEndTime = LocalDateTime.of(2022, 1, 1, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime firstPomodoroStartTime = LocalDateTime.of(2022, 2, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime firstPomodoroEndTime = LocalDateTime.of(2022, 2, 1, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroStartTime = LocalDateTime.of(2022, 2, 2, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime secondPomodoroEndTime = LocalDateTime.of(2022, 2, 2, 7, 20)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime nextPomodoroStartTime = LocalDateTime.of(2022, 3, 1, 7, 0)
                .atZone(ZoneOffset.UTC);
        final ZonedDateTime nextPomodoroEndTime = LocalDateTime.of(2022, 3, 1, 7, 20)
                .atZone(ZoneOffset.UTC);

        PomodoroDto firstPomodoroDto = new PomodoroDto(null, previousPomodoroStartTime, previousPomodoroEndTime, false, List.of());
        PomodoroDto secondPomodoroDto = new PomodoroDto(null, firstPomodoroStartTime, firstPomodoroEndTime, false, List.of());
        PomodoroDto thirdPomodoroDto = new PomodoroDto(null, secondPomodoroStartTime, secondPomodoroEndTime, false, List.of());
        PomodoroDto fourthPomodoroDto = new PomodoroDto(null, nextPomodoroStartTime, nextPomodoroEndTime, false, List.of());

        Pomodoro previousLocalPomodoro = new Pomodoro(null, previousPomodoroStartTime, previousPomodoroEndTime, false, List.of());
        Pomodoro firstLocalPomodoro = new Pomodoro(null, firstPomodoroStartTime, firstPomodoroEndTime, false, List.of());
        Pomodoro secondLocalPomodoro = new Pomodoro(null, secondPomodoroStartTime, secondPomodoroEndTime, false, List.of());
        Pomodoro nextLocalPomodoro = new Pomodoro(null, nextPomodoroStartTime, nextPomodoroEndTime, false, List.of());
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
        doNothing().when(googleDriveService).updatePomodoroData(remotePomodoroDataArgumentCaptor.capture());

        testee.synchronize(SYNCHRONIZATION_BOUND_TIMESTAMP);

        verifyNoMoreInteractions(pomodoroRepository);
        PomodoroDataDto actualRemotePomodoroData = remotePomodoroDataArgumentCaptor.getValue();
        assertThat(actualRemotePomodoroData.getPomodoros())
                .extracting(pomodoro -> Tuple.tuple(pomodoro.getStartTime().withZoneSameInstant(ZoneOffset.UTC),
                        pomodoro.getEndTime().withZoneSameInstant(ZoneOffset.UTC)))
                .containsExactlyInAnyOrderElementsOf(List.of(
                        Tuple.tuple(previousPomodoroStartTime, previousPomodoroEndTime),
                        Tuple.tuple(firstPomodoroStartTime, firstPomodoroEndTime),
                        Tuple.tuple(secondPomodoroStartTime, secondPomodoroEndTime),
                        Tuple.tuple(nextPomodoroStartTime, nextPomodoroEndTime)
                ));
        verify(pomodoroInfoSynchronizationService).save(Boolean.TRUE, SynchronizationResult.UPDATED_REMOTELY, null);
    }

}

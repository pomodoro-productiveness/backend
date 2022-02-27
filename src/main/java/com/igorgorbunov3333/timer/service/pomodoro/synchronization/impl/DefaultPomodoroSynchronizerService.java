package com.igorgorbunov3333.timer.service.pomodoro.synchronization.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroInfoSynchronizationService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroSynchronizerService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class DefaultPomodoroSynchronizerService implements PomodoroSynchronizerService {

    private final PomodoroRepository pomodoroRepository;
    private final GoogleDriveService googleDriveService;
    private final PomodoroInfoSynchronizationService pomodoroInfoSynchronizationService;

    @Async
    @Override
    public void synchronize() {
        try {
            synchronizePomodoros();
        } catch (Exception e) {
            String synchronizationError = e.getMessage() + ". Caused by: " + e.getCause().getMessage();
            savePomodoroInfoSynchronization(Boolean.FALSE, null, synchronizationError);
        }
    }

    private void synchronizePomodoros() {
        List<PomodoroDto> pomodorosFromDataBase = pomodoroRepository.findAll().stream()
                .map(pomodoro -> new PomodoroDto(null, pomodoro.getStartTime(), pomodoro.getEndTime()))
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .map(p -> new PomodoroDto(p.getId(), p.getStartTime(), p.getEndTime()))
                .collect(Collectors.toList());
        PomodoroDataDto pomodoroDataDto = googleDriveService.getPomodoroData();
        List<PomodoroDto> remotePomodoros = pomodoroDataDto.getPomodoros().stream()
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .map(p -> new PomodoroDto(p.getId(), p.getStartTime(), p.getEndTime()))
                .collect(Collectors.toList());
        if (pomodorosFromDataBase.equals(remotePomodoros)) {
            String synchronizationResult = "Nothing to synchronize between remote and local pomodoros";
            savePomodoroInfoSynchronization(Boolean.TRUE, synchronizationResult, null);
            return;
        }

        boolean localPomodorosDoesNotContainAllRemotePomodoros = !pomodorosFromDataBase.containsAll(remotePomodoros);
        boolean remotePomodorosNotContainAnyLocalOrDifferentSize = !remotePomodoros.containsAll(pomodorosFromDataBase);

        List<PomodoroDto> pomodorosToSaveRemotely = getAllSortedDistinctPomodoros(pomodorosFromDataBase, remotePomodoros);
        PomodoroDataDto pomodoroDataToSaveRemotely = new PomodoroDataDto(pomodorosToSaveRemotely);
        if (remotePomodorosNotContainAnyLocalOrDifferentSize) {
            googleDriveService.updatePomodoroData(pomodoroDataToSaveRemotely);
            savePomodoroInfoSynchronization(Boolean.TRUE, "Updated remote pomodoros", null);
        }

        if (localPomodorosDoesNotContainAllRemotePomodoros) {
            List<Pomodoro> pomodorosToSaveLocally = pomodoroDataToSaveRemotely.getPomodoros().stream()
                    .filter(pomodoroDto -> !pomodorosFromDataBase.contains(pomodoroDto))
                    .map(pomodoroDto -> new Pomodoro(null, pomodoroDto.getStartTime(), pomodoroDto.getEndTime()))
                    .collect(Collectors.toList());
            pomodoroRepository.saveAll(pomodorosToSaveLocally);
            savePomodoroInfoSynchronization(Boolean.TRUE, "Updated local pomodoros", null);
        }
    }

    private void savePomodoroInfoSynchronization(Boolean successfullySynchronized,
                                                 String synchronizationResult,
                                                 String synchronizationError) {
        PomodoroSynchronizationInfo pomodoroSynchronizationInfo = PomodoroSynchronizationInfo.builder()
                .timestamp(LocalDateTime.now())
                .synchronizedSuccessfully(successfullySynchronized)
                .synchronizationResult(synchronizationResult)
                .error(synchronizationError)
                .build();
        pomodoroInfoSynchronizationService.save(pomodoroSynchronizationInfo);
    }

    private List<PomodoroDto> getAllSortedDistinctPomodoros(List<PomodoroDto> pomodorosFromDataBase,
                                                            List<PomodoroDto> remotePomodoros) {
        List<PomodoroDto> remotePomodorosToRemove = new ArrayList<>();
        for (PomodoroDto pomodoroFromDb : pomodorosFromDataBase) {
            LocalDateTime startTimeFromDb = pomodoroFromDb.getStartTime();
            LocalDateTime endTimeFromDb = pomodoroFromDb.getEndTime();
            for (PomodoroDto remotePomodoro : remotePomodoros) {
                LocalDateTime remoteStartTime = remotePomodoro.getStartTime();
                if (!startTimeFromDb.equals(remoteStartTime)
                        && remoteStartTime.plusSeconds(2).isAfter(startTimeFromDb)
                        && remoteStartTime.plusSeconds(2).isBefore(endTimeFromDb)) {
                    remotePomodorosToRemove.add(remotePomodoro);
                }
            }
        }
        remotePomodoros.removeAll(remotePomodorosToRemove);
        return Stream.concat(pomodorosFromDataBase.stream(), remotePomodoros.stream())
                .distinct()
                .sorted(Comparator.comparing(PomodoroDto::getEndTime))
                .collect(Collectors.toList());
    }

}

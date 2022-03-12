package com.igorgorbunov3333.timer.service.pomodoro.synchronization.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.commandline.PrinterService;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroInfoSynchronizationService;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroSynchronizerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final PrinterService printerService;

    @Override
    public void synchronizeAfterRemovingPomodoro(LocalDateTime synchronyzationBoundTimestamp) {
        PomodoroSynchronizationInfo pomodoroSynchronizationInfo =
                pomodoroInfoSynchronizationService.getLatestPomodoroSynchronizationInfo()
                .orElse(null);
        boolean needToSynchronize = true;
        String consoleMessage = "";
        if (pomodoroSynchronizationInfo == null) {
            needToSynchronize = false;
            consoleMessage = "Previous synchronization info is not present in database";
        } else if (!pomodoroSynchronizationInfo.getTime().toLocalDate().isEqual(LocalDate.now())) {
            needToSynchronize = false;
            consoleMessage = "Previous synchronization info is for the last day or earlier";
        } else if (Boolean.FALSE.equals(pomodoroSynchronizationInfo.getSynchronizedSuccessfully())) {
            needToSynchronize = false;
            consoleMessage = "Previous synchronization was not successful";
        }
        if (needToSynchronize) {
            try {
                synchronizePomodorosAfterRemovingPomodoro(synchronyzationBoundTimestamp);
            } catch (Exception e) {
                saveWithSynchronizationError(e);
            }
        } else {
            printerService.print("Unable to synchronize. " + consoleMessage);
        }
    }

    @Override
    public void synchronize(LocalDateTime synchronyzationBoundTimestamp) {
        try {
            synchronizePomodoros(synchronyzationBoundTimestamp);
        } catch (Exception e) {
            saveWithSynchronizationError(e);
        }
    }

    private void synchronizePomodorosAfterRemovingPomodoro(LocalDateTime synchronyzationBoundTimestamp) {
        List<PomodoroDto> pomodorosToSaveRemotely = getSortedPomodorosFromDatabase(synchronyzationBoundTimestamp);
        PomodoroDataDto pomodoroDataToSaveRemotely = new PomodoroDataDto(pomodorosToSaveRemotely);
        googleDriveService.updatePomodoroData(pomodoroDataToSaveRemotely);
        pomodoroInfoSynchronizationService.save(Boolean.TRUE, SynchronizationResult.UPDATED_REMOTELY, null);
    }

    private void synchronizePomodoros(LocalDateTime synchronyzationBoundTimestamp) {
        List<PomodoroDto> pomodorosFromDataBase = getSortedPomodorosFromDatabase(synchronyzationBoundTimestamp);
        List<PomodoroDto> remotePomodoros = getSortedRemotePomodoros();
        if (pomodorosFromDataBase.equals(remotePomodoros)) {
            pomodoroInfoSynchronizationService.save(Boolean.TRUE, SynchronizationResult.UPDATED_NOTHING, null);
            return;
        }

        boolean localPomodorosDoesNotContainAllRemotePomodoros = !pomodorosFromDataBase.containsAll(remotePomodoros);
        boolean remotePomodorosNotContainAnyLocalOrDifferentSize = !remotePomodoros.containsAll(pomodorosFromDataBase);

        List<PomodoroDto> pomodorosToSaveRemotely = getAllSortedDistinctPomodoros(pomodorosFromDataBase, remotePomodoros);
        PomodoroDataDto pomodoroDataToSaveRemotely = new PomodoroDataDto(pomodorosToSaveRemotely);
        if (remotePomodorosNotContainAnyLocalOrDifferentSize) {
            googleDriveService.updatePomodoroData(pomodoroDataToSaveRemotely);
            pomodoroInfoSynchronizationService.save(Boolean.TRUE, SynchronizationResult.UPDATED_REMOTELY, null);
        }

        if (localPomodorosDoesNotContainAllRemotePomodoros) {
            List<Pomodoro> pomodorosToSaveLocally = pomodoroDataToSaveRemotely.getPomodoros().stream()
                    .filter(pomodoroDto -> !pomodorosFromDataBase.contains(pomodoroDto))
                    .map(pomodoroDto -> new Pomodoro(null, pomodoroDto.getStartTime(), pomodoroDto.getEndTime()))
                    .collect(Collectors.toList());
            pomodoroRepository.saveAll(pomodorosToSaveLocally);
            pomodoroInfoSynchronizationService.save(Boolean.TRUE, SynchronizationResult.UPDATED_LOCALLY, null);
        }
    }

    private List<PomodoroDto> getSortedPomodorosFromDatabase(LocalDateTime timestamp) {
        return pomodoroRepository.findByEndTimeLessThanEqual(timestamp).stream()
                .map(pomodoro -> new PomodoroDto(null, pomodoro.getStartTime(), pomodoro.getEndTime()))
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .map(p -> new PomodoroDto(p.getId(), p.getStartTime(), p.getEndTime()))
                .collect(Collectors.toList());
    }

    private List<PomodoroDto> getSortedRemotePomodoros() {
        PomodoroDataDto pomodoroDataDto = googleDriveService.getPomodoroData();
        return pomodoroDataDto.getPomodoros().stream()
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .map(p -> new PomodoroDto(p.getId(), p.getStartTime(), p.getEndTime()))
                .collect(Collectors.toList());
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

    private void saveWithSynchronizationError(Exception e) {
        String exceptionName = e.getClass().getName();
        String causeMessage = e.getCause() != null ? e.getCause().getMessage() : "";
        String synchronizationError = exceptionName + ": " + e.getMessage() + ". Caused by: " + causeMessage;
        pomodoroInfoSynchronizationService.save(Boolean.FALSE, null, synchronizationError);
    }

}

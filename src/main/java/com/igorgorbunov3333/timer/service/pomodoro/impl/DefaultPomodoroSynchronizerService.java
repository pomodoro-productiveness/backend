package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroSynchronizerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPomodoroSynchronizerService implements PomodoroSynchronizerService {

    private final PomodoroRepository pomodoroRepository;
    private final GoogleDriveService googleDriveService;

    @Override
    public void synchronize() {
        List<PomodoroDto> pomodorosFromDataBase = pomodoroRepository.findAll().stream()
                .map(pomodoro -> new PomodoroDto(pomodoro.getStartTime(), pomodoro.getEndTime()))
                .collect(Collectors.toList());
        PomodoroDataDto pomodoroDataDto = googleDriveService.getPomodoroData();
        List<PomodoroDto> remotePomodoros = pomodoroDataDto.getPomodoros();

        List<PomodoroDto> pomodorosToSaveRemotely = getAllSortedPomodoros(pomodorosFromDataBase, remotePomodoros);
        PomodoroDataDto pomodoroDataToSaveRemotely = new PomodoroDataDto(pomodorosToSaveRemotely);
        googleDriveService.updatePomodoroData(pomodoroDataToSaveRemotely);
    }

    private List<PomodoroDto> getAllSortedPomodoros(List<PomodoroDto> pomodorosFromDataBase,
                                                    List<PomodoroDto> remotePomodoros) {
        remotePomodoros.addAll(pomodorosFromDataBase);
        Set<PomodoroDto> pomodoroSetToSaveRemotely = new HashSet<>(remotePomodoros);
        List<PomodoroDto> pomodorosListToSaveRemotely = new ArrayList<>(pomodoroSetToSaveRemotely);
        pomodorosListToSaveRemotely.sort(Comparator.comparing(PomodoroDto::getEndTime));
        return pomodorosListToSaveRemotely;
    }

}

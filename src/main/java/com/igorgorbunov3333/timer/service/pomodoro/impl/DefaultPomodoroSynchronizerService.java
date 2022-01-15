package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.MonthlyPomodoroData;
import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.YearlyPomodoroData;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.googledrive.GoogleDriveService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroSynchronizerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPomodoroSynchronizerService implements PomodoroSynchronizerService {

    private final PomodoroRepository pomodoroRepository;
    private final GoogleDriveService googleDriveService;

    @Override
    public void synchronize() {
        List<Pomodoro> pomodorosFromDataBase = pomodoroRepository.findAll();

        PomodoroDataDto remotePomodoroData = googleDriveService.getPomodoroData();
        YearlyPomodoroData latestYearlyPomodoroData = remotePomodoroData.getYearlyPomodoroData().stream()
                .max(Comparator.comparing(YearlyPomodoroData::getYear))
                .orElse(null);
        MonthlyPomodoroData latestMonthlyPomodoroData = null;
        if (latestYearlyPomodoroData != null) {
            latestMonthlyPomodoroData = latestYearlyPomodoroData.getMonthlyPomodoroData().stream()
                    .max(Comparator.comparing(MonthlyPomodoroData::getMonth))
                    .orElse(null);
        }
        PomodoroDto latestPomodoro = null;
        if (latestMonthlyPomodoroData != null) {
            latestPomodoro = latestMonthlyPomodoroData.getPomodoros().stream()
                    .max(Comparator.comparing(PomodoroDto::getStartTime))
                    .orElse(null);
        }
        LocalDate latestRemotePomodoro = null;
        if (latestPomodoro != null) {
            latestRemotePomodoro = latestPomodoro.getStartTime().toLocalDate();
        }
        Map<Integer, Map<Integer, List<Pomodoro>>> yearToMonthPomodorosFromDatabase = pomodorosFromDataBase.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getYear(),
                        Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getMonth().getValue())));
        if (latestRemotePomodoro != null) {
            Integer year = latestRemotePomodoro.getYear();
            Map<Integer, Map<Integer, List<Pomodoro>>> filteredYearToMonthPomodorosFromDatabase = yearToMonthPomodorosFromDatabase.entrySet().stream()
                    .filter(entry -> entry.getKey() >= year)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<Integer, Map<Integer, List<Pomodoro>>> sortedYearToMonthPomodorosFromDatabase = new TreeMap<>(filteredYearToMonthPomodorosFromDatabase);
            Map<Integer, Map<Integer, List<Pomodoro>>> pomodoroDataToSaveRemotely = new HashMap<>();
            for (Map.Entry<Integer, Map<Integer, List<Pomodoro>>> entry : sortedYearToMonthPomodorosFromDatabase.entrySet()) {
                if (entry.getKey().equals(year)) {
                    Integer latestRemotePomodoroMonth = latestRemotePomodoro.getMonthValue();
                    Map<Integer, List<Pomodoro>> monthlyPomodoroData = entry.getValue();
                    //TODO: complete this logic
                }
            }
        } else {
            List<YearlyPomodoroData> yearlyPomodoroData = new ArrayList<>();
            for (Map.Entry<Integer, Map<Integer, List<Pomodoro>>> entry : yearToMonthPomodorosFromDatabase.entrySet()) {
                List<MonthlyPomodoroData> monthlyPomodoroData = new ArrayList<>();
                List<PomodoroDto> pomodoroDtos = new ArrayList<>();
                for (Map.Entry<Integer, List<Pomodoro>> montlyEntry : entry.getValue().entrySet()) {
                    for (Pomodoro pomodoro : montlyEntry.getValue()) {
                        pomodoroDtos.add(new PomodoroDto(pomodoro.getStartTime(), pomodoro.getEndTime()));
                    }
                    MonthlyPomodoroData monthlyData = new MonthlyPomodoroData(montlyEntry.getKey(), pomodoroDtos);
                    monthlyPomodoroData.add(monthlyData);
                }
                YearlyPomodoroData yearlyData = new YearlyPomodoroData(entry.getKey(), monthlyPomodoroData);
                yearlyPomodoroData.add(yearlyData);
            }
            PomodoroDataDto pomodoroDataDto = new PomodoroDataDto(yearlyPomodoroData);
            googleDriveService.updatePomodoroData(pomodoroDataDto);
        }
    }

}

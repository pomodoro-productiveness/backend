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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
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
        LocalDateTime latestRemotePomodoroLocalDate = null;
        if (latestPomodoro != null) {
            latestRemotePomodoroLocalDate = latestPomodoro.getStartTime();
        }
        Map<Integer, Map<Integer, List<Pomodoro>>> yearToMonthPomodorosFromDatabase = pomodorosFromDataBase.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getYear(),
                        Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getMonth().getValue())));
        List<YearlyPomodoroData> yearlyPomodoroDataToSaveRemotely = new ArrayList<>();
        if (latestRemotePomodoroLocalDate != null) {
            Integer latestRemotePomodoroYear = latestRemotePomodoroLocalDate.getYear();
            Map<Integer, Map<Integer, List<Pomodoro>>> filteredYearToMonthPomodorosFromDatabase = yearToMonthPomodorosFromDatabase.entrySet().stream()
                    .filter(entry -> entry.getKey() >= latestRemotePomodoroYear)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<Integer, Map<Integer, List<Pomodoro>>> sortedYearToMonthPomodorosFromDatabase = new TreeMap<>(filteredYearToMonthPomodorosFromDatabase);
            for (Map.Entry<Integer, Map<Integer, List<Pomodoro>>> yearlyEntryFromDatabase : sortedYearToMonthPomodorosFromDatabase.entrySet()) {
                if (yearlyEntryFromDatabase.getKey().equals(latestRemotePomodoroYear)) {
                    Integer latestRemotePomodoroMonth = latestRemotePomodoroLocalDate.getMonthValue();
                    Map<Integer, List<Pomodoro>> monthlyPomodoroData = yearlyEntryFromDatabase.getValue();
                    List<MonthlyPomodoroData> monthlyPomodoroDataToSaveRemotely = new ArrayList<>();
                    for (Map.Entry<Integer, List<Pomodoro>> monthlyEntry : monthlyPomodoroData.entrySet()) {
                        if (monthlyEntry.getKey().equals(latestRemotePomodoroMonth)) {
                            List<PomodoroDto> pomodorosToSaveRemotely = new ArrayList<>();
                            for (Pomodoro pomodoro : monthlyEntry.getValue()) {
                                if (pomodoro.getStartTime().isAfter(latestRemotePomodoroLocalDate)) {
                                    pomodorosToSaveRemotely.add(new PomodoroDto(pomodoro.getStartTime(), pomodoro.getEndTime()));
                                }
                            }
                            monthlyPomodoroDataToSaveRemotely.add(new MonthlyPomodoroData(monthlyEntry.getKey(), pomodorosToSaveRemotely));
                        } else if (monthlyEntry.getKey() > latestRemotePomodoroMonth) {
                            List<MonthlyPomodoroData> monthlyDataToSave = mapToMonthlyPomodoroData(Map.of(monthlyEntry.getKey(), monthlyEntry.getValue()));
                            monthlyPomodoroDataToSaveRemotely.addAll(monthlyDataToSave);
                        }
                    }
                    yearlyPomodoroDataToSaveRemotely.add(new YearlyPomodoroData(yearlyEntryFromDatabase.getKey(), monthlyPomodoroDataToSaveRemotely));
                }
                if (!yearlyPomodoroDataToSaveRemotely.stream().map(YearlyPomodoroData::getYear).collect(Collectors.toSet()).contains(yearlyEntryFromDatabase.getKey())) {
                    List<MonthlyPomodoroData> monthlyPomodoroData = mapToMonthlyPomodoroData(yearlyEntryFromDatabase.getValue());
                    YearlyPomodoroData yearlyPomodoroData = new YearlyPomodoroData(yearlyEntryFromDatabase.getKey(), monthlyPomodoroData);
                    yearlyPomodoroDataToSaveRemotely.add(yearlyPomodoroData);
                }
            }
            Map<Integer, YearlyPomodoroData> oldYearlyPomodoroRemoteData = remotePomodoroData.getYearlyPomodoroData().stream()
                    .collect(Collectors.toMap(YearlyPomodoroData::getYear, Function.identity()));
            Set<YearlyPomodoroData> allYearlyRemotePomodoroDataToSave = new HashSet<>(remotePomodoroData.getYearlyPomodoroData());
            for (YearlyPomodoroData newYearlyPomodoroData : yearlyPomodoroDataToSaveRemotely) {
                if (oldYearlyPomodoroRemoteData.get(newYearlyPomodoroData.getYear()) != null) {
                    YearlyPomodoroData oldYearPomodoroData = oldYearlyPomodoroRemoteData.get(newYearlyPomodoroData.getYear());
                    List<MonthlyPomodoroData> oldMonthlyPomodoroData = oldYearPomodoroData.getMonthlyPomodoroData();
                    MonthlyPomodoroData oldestNewMonthlyData = newYearlyPomodoroData.getMonthlyPomodoroData().stream()
                            .min(Comparator.comparing(MonthlyPomodoroData::getMonth))
                            .get();
                    MonthlyPomodoroData latestOldMonthlyData = oldMonthlyPomodoroData.stream()
                            .max(Comparator.comparing(MonthlyPomodoroData::getMonth))
                            .get();
                    if (oldestNewMonthlyData.getMonth().equals(latestOldMonthlyData.getMonth())) {
                        Set<PomodoroDto> monthlyPomodoros = new HashSet<>(oldestNewMonthlyData.getPomodoros());
                        monthlyPomodoros.addAll(latestOldMonthlyData.getPomodoros());
                        MonthlyPomodoroData monthlyPomodoroDataToAdd = new MonthlyPomodoroData(oldestNewMonthlyData.getMonth(), new ArrayList<>(monthlyPomodoros));
                        List<MonthlyPomodoroData> newMonthlyDataWithoutCommonMonth = newYearlyPomodoroData.getMonthlyPomodoroData().stream()
                                .filter(month -> !month.getMonth().equals(monthlyPomodoroDataToAdd.getMonth()))
                                .collect(Collectors.toList());
                        List<MonthlyPomodoroData> oldMonthlyDataWithoutCommonMonth = oldYearPomodoroData.getMonthlyPomodoroData().stream()
                                .filter(month -> !month.getMonth().equals(monthlyPomodoroDataToAdd.getMonth()))
                                .collect(Collectors.toList());
                        List<MonthlyPomodoroData> monthlyPomodoroDataToSave = new ArrayList<>(newMonthlyDataWithoutCommonMonth);
                        monthlyPomodoroDataToSave.addAll(oldMonthlyDataWithoutCommonMonth);
                        monthlyPomodoroDataToSave.add(monthlyPomodoroDataToAdd);
                        allYearlyRemotePomodoroDataToSave.add(new YearlyPomodoroData(newYearlyPomodoroData.getYear(), monthlyPomodoroDataToSave));
                    }
                } else {
                    allYearlyRemotePomodoroDataToSave.add(newYearlyPomodoroData);
                }
            }
            googleDriveService.updatePomodoroData(new PomodoroDataDto(new ArrayList<>(allYearlyRemotePomodoroDataToSave)));
        } else {
            List<YearlyPomodoroData> yearlyPomodoroData = new ArrayList<>();
            for (Map.Entry<Integer, Map<Integer, List<Pomodoro>>> entry : yearToMonthPomodorosFromDatabase.entrySet()) {
                List<MonthlyPomodoroData> monthlyPomodoroData = mapToMonthlyPomodoroData(entry.getValue());
                YearlyPomodoroData yearlyData = new YearlyPomodoroData(entry.getKey(), monthlyPomodoroData);
                yearlyPomodoroData.add(yearlyData);
            }
            PomodoroDataDto pomodoroDataDto = new PomodoroDataDto(yearlyPomodoroData);
            googleDriveService.updatePomodoroData(pomodoroDataDto);
        }
    }

    private List<MonthlyPomodoroData> mapToMonthlyPomodoroData(Map<Integer, List<Pomodoro>> monthToPomodoros) {
        List<MonthlyPomodoroData> monthlyPomodoroData = new ArrayList<>();
        List<PomodoroDto> pomodoroDtos = new ArrayList<>();
        for (Map.Entry<Integer, List<Pomodoro>> montlyEntry : monthToPomodoros.entrySet()) {
            for (Pomodoro pomodoro : montlyEntry.getValue()) {
                pomodoroDtos.add(new PomodoroDto(pomodoro.getStartTime(), pomodoro.getEndTime()));
            }
            MonthlyPomodoroData monthlyData = new MonthlyPomodoroData(montlyEntry.getKey(), pomodoroDtos);
            monthlyPomodoroData.add(monthlyData);
        }
        return monthlyPomodoroData;
    }

}

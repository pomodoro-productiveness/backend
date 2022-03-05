package com.igorgorbunov3333.timer.service.pomodoro.synchronization.impl;

import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.repository.PomodoroSynchronizationInfoRepository;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroInfoSynchronizationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DefaultPomodoroInfoSynchronizationService implements PomodoroInfoSynchronizationService {

    private final PomodoroSynchronizationInfoRepository pomodoroSynchronizationInfoRepository;

    @Override
    @Transactional
    public void save(@NonNull Boolean newSynchronizationResult,
                     SynchronizationResult synchronizationResult,
                     String synchronizationError) {
        LocalDateTime nowTimestamp = LocalDateTime.now();
        LocalDateTime sevenDaysAgoTimestamp = nowTimestamp.minusDays(7L);
        List<PomodoroSynchronizationInfo> pomodoroSynchronizationInfosBeforeSevenDays =
                pomodoroSynchronizationInfoRepository.findByTimeBefore(sevenDaysAgoTimestamp);
        pomodoroSynchronizationInfoRepository.deleteAll(pomodoroSynchronizationInfosBeforeSevenDays);
        pomodoroSynchronizationInfoRepository.flush();
        PomodoroSynchronizationInfo latestPomodoroSynchronizationInfo = getLatestPomodoroSynchronizationInfo()
                .orElse(null);
        Long previousSynchronizationInfoId = null;
        if (latestPomodoroSynchronizationInfo != null
                && latestPomodoroSynchronizationInfo.getTime().toLocalDate().equals(LocalDate.now())) {
            previousSynchronizationInfoId = latestPomodoroSynchronizationInfo.getId();
        }
        PomodoroSynchronizationInfo pomodoroSynchronizationInfo = PomodoroSynchronizationInfo.builder()
                .id(previousSynchronizationInfoId)
                .time(nowTimestamp)
                .synchronizedSuccessfully(newSynchronizationResult)
                .synchronizationResult(synchronizationResult.name())
                .synchronizationError(synchronizationError)
                .build();
        pomodoroSynchronizationInfoRepository.save(pomodoroSynchronizationInfo);
    }

    @Override
    public Optional<PomodoroSynchronizationInfo> getLatestPomodoroSynchronizationInfo() {
        return pomodoroSynchronizationInfoRepository.findTopByOrderByTimeDesc();
    }

}

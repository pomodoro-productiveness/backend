package com.igorgorbunov3333.timer.service.pomodoro.synchronization.impl;

import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.repository.PomodoroSynchronizationInfoRepository;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroInfoSynchronizationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DefaultPomodoroInfoSynchronizationService implements PomodoroInfoSynchronizationService {

    private final PomodoroSynchronizationInfoRepository pomodoroSynchronizationInfoRepository;

    @Override
    @Transactional
    public void save(@NonNull Boolean successfullySynchronized,
                     SynchronizationResult synchronizationResult,
                     String synchronizationError) {
        LocalDateTime nowTimestamp = LocalDateTime.now();
        LocalDateTime sevenDaysAgoTimestamp = nowTimestamp.minusDays(7L);
        List<PomodoroSynchronizationInfo> pomodoroSynchronizationInfosBeforeSevenDays =
                pomodoroSynchronizationInfoRepository.findByTimeBefore(sevenDaysAgoTimestamp);
        pomodoroSynchronizationInfoRepository.deleteAll(pomodoroSynchronizationInfosBeforeSevenDays);
        pomodoroSynchronizationInfoRepository.flush();
        PomodoroSynchronizationInfo pomodoroSynchronizationInfo = PomodoroSynchronizationInfo.builder()
                .time(nowTimestamp)
                .synchronizedSuccessfully(successfullySynchronized)
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

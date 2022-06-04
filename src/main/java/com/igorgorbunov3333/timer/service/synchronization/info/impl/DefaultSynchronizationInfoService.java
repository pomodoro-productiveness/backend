package com.igorgorbunov3333.timer.service.synchronization.info.impl;

import com.igorgorbunov3333.timer.model.entity.SynchronizationInfo;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.repository.SynchronizationInfoRepository;
import com.igorgorbunov3333.timer.service.synchronization.info.SynchronizationInfoService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DefaultSynchronizationInfoService implements SynchronizationInfoService {

    private final SynchronizationInfoRepository synchronizationInfoRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(@NonNull Boolean newSynchronizationResult,
                     SynchronizationResult synchronizationResult,
                     String synchronizationError) {
        synchronizationInfoRepository.deleteAll();
        synchronizationInfoRepository.flush();

        SynchronizationInfo synchronizationInfo = SynchronizationInfo.builder()
                .time(LocalDateTime.now())
                .synchronizedSuccessfully(newSynchronizationResult)
                .synchronizationResult(synchronizationResult.name())
                .synchronizationError(synchronizationError)
                .build();
        synchronizationInfoRepository.save(synchronizationInfo);
    }

    @Override
    public Optional<SynchronizationInfo> getLatestPomodoroSynchronizationInfo() {
        return synchronizationInfoRepository.findTopByOrderByTimeDesc();
    }

}

package com.igorgorbunov3333.timer.service.pomodoro.synchronization.impl;

import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import com.igorgorbunov3333.timer.repository.PomodoroSynchronizationInfoRepository;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroInfoSynchronizationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultPomodoroInfoSynchronizationService implements PomodoroInfoSynchronizationService {

    private final PomodoroSynchronizationInfoRepository pomodoroSynchronizationInfoRepository;

    @Override
    public void save(@NonNull PomodoroSynchronizationInfo pomodoroSynchronizationInfo) {
        pomodoroSynchronizationInfoRepository.save(pomodoroSynchronizationInfo);
    }

}

package com.igorgorbunov3333.timer.service.synchronization.priority.local;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;
import com.igorgorbunov3333.timer.service.pomodoro.RemotePomodoroDataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocalPrioritySynchronizer {

    private final RemotePomodoroDataService remotePomodoroDataService;
    private final LocalSynchronizationDataProvider localSynchronizationDataProvider;

    public void synchronize() {
        try {
            PomodoroDataDto data = localSynchronizationDataProvider.validatePreviousRemotePrioritySynchronizationAndProvide();
            remotePomodoroDataService.updateRemoteData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

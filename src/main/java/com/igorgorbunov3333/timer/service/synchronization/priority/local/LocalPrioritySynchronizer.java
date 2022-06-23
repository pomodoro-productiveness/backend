package com.igorgorbunov3333.timer.service.synchronization.priority.local;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroMetadataDto;
import com.igorgorbunov3333.timer.service.pomodoro.provider.remote.RemotePomodoroDataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocalPrioritySynchronizer {

    private final RemotePomodoroDataService remotePomodoroDataService;
    private final LocalSynchronizationDataProvider localSynchronizationDataProvider;

    public void synchronize() {
        try {
            PomodoroMetadataDto data = localSynchronizationDataProvider.validatePreviousRemotePrioritySynchronizationAndProvide();
            remotePomodoroDataService.updateRemoteData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

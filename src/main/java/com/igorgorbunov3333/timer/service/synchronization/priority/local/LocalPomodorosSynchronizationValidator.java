package com.igorgorbunov3333.timer.service.synchronization.priority.local;

import com.igorgorbunov3333.timer.model.entity.SynchronizationInfo;
import com.igorgorbunov3333.timer.service.exception.PomodoroSynchronizationException;
import com.igorgorbunov3333.timer.service.synchronization.info.SynchronizationInfoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class LocalPomodorosSynchronizationValidator {

    private final SynchronizationInfoService synchronizationInfoService;

    public void validatePomodoroSynchronizationInfo() {
        SynchronizationInfo synchronizationInfo =
                synchronizationInfoService.getLatestPomodoroSynchronizationInfo()
                        .orElse(null);

        if (synchronizationInfo == null) {
            throw new PomodoroSynchronizationException("Previous synchronization info is not present in database");
        }
        if (!synchronizationInfo.getTime().toLocalDate().isEqual(LocalDate.now())) {
            throw new PomodoroSynchronizationException("Previous synchronization info is for the last day or earlier");
        }
        if (Boolean.FALSE.equals(synchronizationInfo.getSynchronizedSuccessfully())) {
            throw new PomodoroSynchronizationException("Previous synchronization was not successful");
        }
    }

}

package com.igorgorbunov3333.timer.service.synchronization.priority.remote;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.pomodoro.RemotePomodoroDataService;
import com.igorgorbunov3333.timer.service.synchronization.Synchronizer;
import com.igorgorbunov3333.timer.service.synchronization.enums.SynchronizationPriorityType;
import com.igorgorbunov3333.timer.service.synchronization.info.SynchronizationInfoService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class RemotePrioritySynchronizer implements Synchronizer {

    private final RemotePomodoroDataService remotePomodoroDataService;
    private final PomodoroService pomodoroService;
    private final TagService tagService;
    private final SynchronizationInfoService synchronizationInfoService;

    @Override
    @Transactional
    public void synchronize() {
        try {
            saveRemoteDataLocally();
            synchronizationInfoService.save(Boolean.TRUE, SynchronizationResult.SUCCESSFULLY, null);
        } catch (Exception e) {
            String exceptionName = e.getClass().getName();
            String causeMessage = e.getCause() != null ? e.getCause().getMessage() : StringUtils.EMPTY;
            String errorMessage = exceptionName + ": " + e.getMessage() + ". Caused by: " + causeMessage;;
            synchronizationInfoService.save(Boolean.FALSE, SynchronizationResult.FAILED, errorMessage);
            throw e;
        }
    }

    @Override
    public SynchronizationPriorityType synchronizationType() {
        return SynchronizationPriorityType.REMOTE;
    }

    void saveRemoteDataLocally() {
        PomodoroDataDto remotePomodoroDataDto = remotePomodoroDataService.getRemoteData();

        pomodoroService.removeAllPomodoros();
        tagService.removeAllTags();

        List<PomodoroTagDto> tags = remotePomodoroDataDto.getPomodoroTags();
        List<PomodoroTag> savedTags = saveTags(tags);
        pomodoroService.save(remotePomodoroDataDto.getPomodoros(), savedTags);
    }

    private List<PomodoroTag> saveTags(List<PomodoroTagDto> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        return tagService.save(tags);
    }

}

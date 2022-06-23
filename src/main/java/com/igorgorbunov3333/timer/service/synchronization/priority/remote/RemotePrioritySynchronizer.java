package com.igorgorbunov3333.timer.service.synchronization.priority.remote;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroMetadataDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.service.dayoff.DayOffSynchronizer;
import com.igorgorbunov3333.timer.service.pomodoro.remover.LocalPomodoroRemover;
import com.igorgorbunov3333.timer.service.pomodoro.saver.PomodoroSaver;
import com.igorgorbunov3333.timer.service.synchronization.info.SynchronizationInfoService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class RemotePrioritySynchronizer {

    private final LocalPomodoroRemover localPomodoroRemover;
    private final TagService tagService;
    private final SynchronizationInfoService synchronizationInfoService;
    private final PomodoroSaver pomodoroSaver;
    private final DayOffSynchronizer dayOffSynchronizer;

    @Transactional
    public void synchronize(@NonNull PomodoroMetadataDto remoteData) {
        try {
            saveRemoteDataLocally(remoteData);
            synchronizationInfoService.save(Boolean.TRUE, SynchronizationResult.SUCCESSFULLY, null);
            dayOffSynchronizer.synchronize();
        } catch (Exception e) {
            String exceptionName = e.getClass().getName();
            String causeMessage = e.getCause() != null ? e.getCause().getMessage() : StringUtils.EMPTY;
            String errorMessage = exceptionName + ": " + e.getMessage() + ". Caused by: " + causeMessage;;
            synchronizationInfoService.save(Boolean.FALSE, SynchronizationResult.FAILED, errorMessage);
            throw e;
        }
    }

    private void saveRemoteDataLocally(PomodoroMetadataDto remotePomodoroMetadataDto) {
        localPomodoroRemover.removeAll();
        tagService.removeAllTags();

        List<PomodoroTagDto> tags = remotePomodoroMetadataDto.getPomodoroTags();
        List<PomodoroTag> savedTags = saveTags(tags);
        List<PomodoroDto> pomodoroDtoToSave = remotePomodoroMetadataDto.getPomodoros();
        pomodoroSaver.save(pomodoroDtoToSave, savedTags);
    }

    private List<PomodoroTag> saveTags(List<PomodoroTagDto> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        return tagService.save(tags);
    }

}

package com.igorgorbunov3333.timer.service.synchronization.priority.local;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LocalSynchronizationDataProvider {

    private final LocalPomodorosSynchronizationValidator localPomodorosSynchronizationValidator;
    private final PomodoroService pomodoroService;
    private final TagService tagService;

    public PomodoroDataDto validatePreviousRemotePrioritySynchronizationAndProvide() {
        localPomodorosSynchronizationValidator.validatePomodoroSynchronizationInfo();
        List<PomodoroDto> pomodoros = pomodoroService.getAllSortedPomodoros();
        List<PomodoroTagDto> tags = tagService.getSortedTags(true);

        return new PomodoroDataDto(pomodoros, tags);
    }

}

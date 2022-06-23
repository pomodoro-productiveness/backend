package com.igorgorbunov3333.timer.service.synchronization.priority.local;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroMetadataDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LocalSynchronizationDataProvider {

    private final LocalPomodorosSynchronizationValidator localPomodorosSynchronizationValidator;
    private final PomodoroRepository pomodoroRepository;
    private final TagService tagService;
    private final PomodoroMapper pomodoroMapper;

    public PomodoroMetadataDto validatePreviousRemotePrioritySynchronizationAndProvide() {
        localPomodorosSynchronizationValidator.validatePomodoroSynchronizationInfo();
        List<PomodoroDto> pomodoros = pomodoroRepository.findAll().stream()
                .map(pomodoroMapper::mapToDto)
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .collect(Collectors.toList());
        List<PomodoroTagDto> tags = tagService.getSortedTags(true);

        return new PomodoroMetadataDto(pomodoros, tags);
    }

}

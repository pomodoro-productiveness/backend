package com.igorgorbunov3333.timer.backend.service.pomodoro.updater;

import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.tag.group.PomodoroTagGroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class LocalPomodoroUpdater {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroTagGroupService pomodoroTagGroupService;

    @Transactional
    public void updatePomodoroWithTag(List<Long> pomodoroId, Set<String> tagNames) {
        Optional<PomodoroTagGroup> tagGroup = pomodoroTagGroupService.findTagGroupsByTagNames(tagNames);

        tagGroup.ifPresent(group -> update(group, pomodoroId));
    }

    private void update(PomodoroTagGroup tagGroup, List<Long> pomodoroId) {
        List<Pomodoro> pomodoro = pomodoroRepository.findByIdIn(pomodoroId);

        pomodoro.forEach(p -> p.setPomodoroTagGroup(tagGroup));

        pomodoroRepository.saveAll(pomodoro);
    }

}

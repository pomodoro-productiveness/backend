package com.igorgorbunov3333.timer.service.pomodoro.updater;

import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.PomodoroTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class LocalPomodoroUpdater {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroTagRepository pomodoroTagRepository;

    @Transactional
    public void updatePomodoroWithTagsByNewTags(Set<String> oldTags, Set<String> newTags) {
        List<Pomodoro> pomodoroWithOldTags = pomodoroRepository.findByTagsNameIn(oldTags);

        pomodoroWithOldTags
                .forEach(p -> updatePomodoroWithTag(List.of(p.getId()), newTags));
    }

    @Transactional
    public void updatePomodoroWithTag(List<Long> pomodoroId, Set<String> tagNames) {
        pomodoroId.forEach(id -> update(id, tagNames));
    }

    private void update(Long pomodoroId, Set<String> tagNames) {
        Pomodoro pomodoroWithTag = pomodoroRepository.getById(pomodoroId);
        List<PomodoroTag> tags = pomodoroTagRepository.findByNameIn(tagNames);

        pomodoroWithTag.setTags(tags);
        pomodoroRepository.save(pomodoroWithTag);
    }

}

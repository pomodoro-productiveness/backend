package com.igorgorbunov3333.timer.service.tag.bunch;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagBunch;
import com.igorgorbunov3333.timer.repository.PomodoroTagBunchRepository;
import com.igorgorbunov3333.timer.repository.PomodoroTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class PomodoroTagBunchService {

    private final PomodoroTagBunchRepository pomodoroTagBunchRepository;
    private final PomodoroTagRepository pomodoroTagRepository;

    public List<PomodoroTagBunch> getLatestTagBunches() {
        return pomodoroTagBunchRepository.findAllByOrderByIdDesc();
    }

    public void saveBunch(Set<String> tags) {
        List<PomodoroTag> pomodoroTags = pomodoroTagRepository.findByNameIn(tags);
        List<PomodoroTagBunch> allBunches = pomodoroTagBunchRepository.findAll();

        for (PomodoroTagBunch bunch : allBunches) {
            if (new HashSet<>(bunch.getPomodoroTags()).equals(new HashSet<>(pomodoroTags))) {
                return;
            }
        }

        if (!CollectionUtils.isEmpty(pomodoroTags)) {
            PomodoroTagBunch bunch = new PomodoroTagBunch(null, pomodoroTags);

            pomodoroTagBunchRepository.save(bunch);
        }
    }

}

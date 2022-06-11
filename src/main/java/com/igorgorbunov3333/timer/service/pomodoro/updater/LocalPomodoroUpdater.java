package com.igorgorbunov3333.timer.service.pomodoro.updater;

import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LocalPomodoroUpdater {

    private final PomodoroRepository pomodoroRepository;
    private final TagRepository tagRepository;

    public void updatePomodoroWithTag(Long pomodoroId, String tagName) {
        Pomodoro pomodoroWithTag = pomodoroRepository.getById(pomodoroId);
        PomodoroTag tag = tagRepository.findByName(tagName).orElse(null);

        if (tag == null) {
            throw new IllegalArgumentException(String.format("No tag with name %s", tagName)); //TODO: use another exception?
        }

        pomodoroWithTag.setTag(tag);
        pomodoroRepository.save(pomodoroWithTag);
    }

}

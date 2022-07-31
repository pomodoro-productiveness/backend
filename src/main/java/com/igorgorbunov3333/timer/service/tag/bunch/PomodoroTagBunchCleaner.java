package com.igorgorbunov3333.timer.service.tag.bunch;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagBunch;
import com.igorgorbunov3333.timer.repository.PomodoroTagBunchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroTagBunchCleaner {

    private final PomodoroTagBunchRepository pomodoroTagBunchRepository;

    @Transactional
    public void clean() {
        List<PomodoroTagBunch> bunchesToDelete = pomodoroTagBunchRepository.findTop10ByOrderByOrderNumberDesc()
                .stream()
                .skip(10L)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(bunchesToDelete)) {
            return;
        }

        pomodoroTagBunchRepository.deleteAll(bunchesToDelete);
        pomodoroTagBunchRepository.flush();
    }

}

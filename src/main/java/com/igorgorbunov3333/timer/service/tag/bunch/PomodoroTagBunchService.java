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
        return pomodoroTagBunchRepository.findTop10ByOrderByOrderNumberDesc();
    }

    public void saveBunch(Set<String> tags) {
        List<PomodoroTag> pomodoroTags = pomodoroTagRepository.findByNameIn(tags);
        List<PomodoroTagBunch> allBunches = pomodoroTagBunchRepository.findAll();

        for (PomodoroTagBunch bunch : allBunches) {
            if (new HashSet<>(bunch.getPomodoroTags()).equals(new HashSet<>(pomodoroTags))) {
                updateOrderNumber(bunch, allBunches);
                return;
            }
        }

        if (!CollectionUtils.isEmpty(pomodoroTags)) {
            long nextOrderNumber = calculateNextOrderNumber(allBunches);

            PomodoroTagBunch bunch = new PomodoroTagBunch(null, pomodoroTags, nextOrderNumber);

            pomodoroTagBunchRepository.save(bunch);
        }
    }

    public void updateOrderNumber(PomodoroTagBunch bunch, List<PomodoroTagBunch> allBunches) {
        if (allBunches == null) {
            allBunches = pomodoroTagBunchRepository.findAll();
        }

        long nextOrderNumber = calculateNextOrderNumber(allBunches);

        bunch.setOrderNumber(nextOrderNumber);

        pomodoroTagBunchRepository.save(bunch);
        pomodoroTagBunchRepository.flush();
    }

    private long calculateNextOrderNumber(List<PomodoroTagBunch> allBunches) {
        if (!CollectionUtils.isEmpty(allBunches)) {
            long maxOrderNumber = allBunches.stream()
                    .mapToLong(PomodoroTagBunch::getOrderNumber)
                    .max()
                    .orElse(1L);

            return maxOrderNumber + 1L;
        }
        return 1;
    }

}

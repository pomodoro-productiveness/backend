package com.igorgorbunov3333.timer.service.tag.group;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.repository.PomodoroTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroTagGroupService {

    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final PomodoroTagRepository pomodoroTagRepository;

    public List<PomodoroTagGroup> getLatestTagGroups() {
        return pomodoroTagGroupRepository.findTop30ByOrderByOrderNumberDesc()
                .stream()
                .map(this::getSortedTags)
                .collect(Collectors.toList());
    }

    public void saveTagGroup(Set<String> tags) {
        List<PomodoroTag> pomodoroTags = pomodoroTagRepository.findByNameIn(tags);
        List<PomodoroTagGroup> allGroups = pomodoroTagGroupRepository.findAll();

        for (PomodoroTagGroup group : allGroups) {
            if (new HashSet<>(group.getPomodoroTags()).equals(new HashSet<>(pomodoroTags))) {
                updateOrderNumber(group, allGroups);
                return;
            }
        }

        if (!CollectionUtils.isEmpty(pomodoroTags)) {
            long nextOrderNumber = calculateNextOrderNumber(allGroups);

            PomodoroTagGroup group = new PomodoroTagGroup(null, new HashSet<>(pomodoroTags), nextOrderNumber);

            pomodoroTagGroupRepository.save(group);
        }
    }

    public void updateOrderNumber(PomodoroTagGroup group, List<PomodoroTagGroup> allGroups) {
        if (allGroups == null) {
            allGroups = pomodoroTagGroupRepository.findAll();
        }

        long nextOrderNumber = calculateNextOrderNumber(allGroups);

        group.setOrderNumber(nextOrderNumber);

        pomodoroTagGroupRepository.save(group);
        pomodoroTagGroupRepository.flush();
    }

    private long calculateNextOrderNumber(List<PomodoroTagGroup> allGroups) {
        if (!CollectionUtils.isEmpty(allGroups)) {
            long maxOrderNumber = allGroups.stream()
                    .mapToLong(PomodoroTagGroup::getOrderNumber)
                    .max()
                    .orElse(1L);

            return maxOrderNumber + 1L;
        }
        return 1;
    }

    public Optional<PomodoroTagGroup> findTagGroupsByTagNames(Set<String> tagNames) {
        return pomodoroTagGroupRepository.findAll().stream()
                .filter(group -> group.getPomodoroTags().stream().map(PomodoroTag::getName).collect(Collectors.toSet()).equals(tagNames))
                .findFirst();
    }

    private PomodoroTagGroup getSortedTags(PomodoroTagGroup tagGroup) {
        Set<PomodoroTag> tags = new TreeSet<>(tagGroup.getPomodoroTags());

        return new PomodoroTagGroup(tagGroup.getId(), tags, tagGroup.getOrderNumber());
    }

}

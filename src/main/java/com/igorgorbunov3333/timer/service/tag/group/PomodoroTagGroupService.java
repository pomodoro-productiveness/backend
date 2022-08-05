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
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroTagGroupService {

    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final PomodoroTagRepository pomodoroTagRepository;

    public List<PomodoroTagGroup> getLatestTagGroups() {
        return pomodoroTagGroupRepository.findTop10ByOrderByOrderNumberDesc();
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

            PomodoroTagGroup group = new PomodoroTagGroup(null, pomodoroTags, nextOrderNumber);

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

    public void updateTagGroup(PomodoroTagGroup tagGroupToUpdate, Set<String> newTags) {
        List<PomodoroTagGroup> pomodoroTagGroups = pomodoroTagGroupRepository.findAll();

        for (PomodoroTagGroup group : pomodoroTagGroups) {
            Set<String> groupTags = group.getPomodoroTags().stream()
                    .map(PomodoroTag::getName)
                    .collect(Collectors.toSet());

            if (groupTags.equals(newTags)) {
                throw new RuntimeException("Tag group with following tags [" + groupTags + "] already exists");
            }
        }

        List<PomodoroTag> newTagsToMap = pomodoroTagRepository.findByNameIn(newTags);

        tagGroupToUpdate.setPomodoroTags(newTagsToMap);

        pomodoroTagGroupRepository.save(tagGroupToUpdate);
    }

}

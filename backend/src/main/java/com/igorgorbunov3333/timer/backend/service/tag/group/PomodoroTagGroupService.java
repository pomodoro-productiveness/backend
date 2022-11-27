package com.igorgorbunov3333.timer.backend.service.tag.group;

import com.igorgorbunov3333.timer.backend.service.exception.EntityAlreadyExists;
import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroTagGroupMapper;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagRepository;
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
    private final PomodoroTagGroupMapper pomodoroTagGroupMapper;

    public List<PomodoroTagGroupDto> getTagGroupsOrderedByFrequencyOfUse() {
        return pomodoroTagGroupRepository.findByOrderByOrderNumberDesc()
                .stream()
                .map(this::getSortedTags)
                .map(pomodoroTagGroupMapper::toDto)
                .toList();
    }

    public PomodoroTagGroupDto saveTagGroup(Set<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            throw new IllegalArgumentException("Tags for save must not be mull or empty");
        }

        List<PomodoroTag> pomodoroTags = pomodoroTagRepository.findByNameIn(tags);

        if (CollectionUtils.isEmpty(pomodoroTags)) {
            throw new EntityDoesNotExist("No such tags: " + tags + " present in database");
        }

        if (pomodoroTags.size() != tags.size()) {
            throw new EntityDoesNotExist("Not all of tags: " + tags + " present in database");
        }

        List<PomodoroTagGroup> allGroups = pomodoroTagGroupRepository.findAll();

        for (PomodoroTagGroup group : allGroups) {
            if (new HashSet<>(group.getPomodoroTags()).equals(new HashSet<>(pomodoroTags))) {
                throw new EntityAlreadyExists(PomodoroTagGroup.class.getSimpleName() + " with tags " + tags + " already exists");
            }
        }

        long nextOrderNumber = calculateNextOrderNumber(allGroups);
        PomodoroTagGroup group = new PomodoroTagGroup(null, new HashSet<>(pomodoroTags), nextOrderNumber);

        PomodoroTagGroup savedGroup = pomodoroTagGroupRepository.save(group);

        return pomodoroTagGroupMapper.toDto(savedGroup);
    }

    public PomodoroTagGroupDto updateTagGroupWithTags(long tagGroupId, Set<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
            throw new IllegalArgumentException(String.format("Tag names collection for updating %s must not be empty", PomodoroTagGroup.class.getSimpleName()));
        }

        PomodoroTagGroup tagGroupToUpdate = pomodoroTagGroupRepository.findById(tagGroupId)
                .orElse(null);

        if (tagGroupToUpdate == null) {
            throw new EntityDoesNotExist(String.format("%s with [%d] does not exists", PomodoroTagGroup.class.getSimpleName(), tagGroupId));
        }

        List<PomodoroTag> tags = pomodoroTagRepository.findByNameIn(tagNames);

        for (PomodoroTag tag : tags) {
            if (!tagNames.contains(tag.getName())) {
                throw new EntityDoesNotExist(String.format("Tag with name %s does not exist", tag.getName()));
            }
        }

        tagGroupToUpdate.setPomodoroTags(new HashSet<>(tags));

        PomodoroTagGroup updatedPomodoroTagGroup = pomodoroTagGroupRepository.save(tagGroupToUpdate);

        return pomodoroTagGroupMapper.toDto(updatedPomodoroTagGroup);
    }

    private long calculateNextOrderNumber(List<PomodoroTagGroup> allGroups) {
        if (CollectionUtils.isEmpty(allGroups)) {
            return 1L;
        }

        long maxOrderNumber = allGroups.stream()
                .mapToLong(PomodoroTagGroup::getOrderNumber)
                .max()
                .orElse(0L);

        return maxOrderNumber + 1L;
    }

    public Optional<PomodoroTagGroup> findTagGroupsByTagNames(Set<String> tagNames) {
        return pomodoroTagGroupRepository.findAll().stream()
                .filter(group -> group.getPomodoroTags().stream()
                        .map(PomodoroTag::getName)
                        .collect(Collectors.toSet())
                        .equals(tagNames))
                .findFirst();
    }

    private PomodoroTagGroup getSortedTags(PomodoroTagGroup tagGroup) {
        Set<PomodoroTag> tags = new TreeSet<>(tagGroup.getPomodoroTags());

        return new PomodoroTagGroup(tagGroup.getId(), tags, tagGroup.getOrderNumber());
    }

}

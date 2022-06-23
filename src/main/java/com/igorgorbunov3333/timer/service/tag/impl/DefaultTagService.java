package com.igorgorbunov3333.timer.service.tag.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.TagRepository;
import com.igorgorbunov3333.timer.service.exception.TagOperationException;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultTagService implements TagService {

    private final TagRepository tagRepository;
    private final PomodoroRepository pomodoroRepository;

    @Override
    public String save(String name) {
        if (pomodoroRepository.existsByTagName(name)) {
            throw new TagOperationException(String.format("Tag with name %s already marked as deleted", name));
        }

        PomodoroTag tag = new PomodoroTag(null, name, null, Collections.emptyList(), false);
        PomodoroTag savedTag = tagRepository.save(tag);

        return savedTag.getName();
    }

    //TODO: use mapper
    @Override
    @Transactional(readOnly = true)
    public List<PomodoroTagDto> getSortedTags(boolean withRemoved) {
        return tagRepository.findByParentIsNull().stream()
                .filter(tag -> filterTag(tag, withRemoved))
                .map(tag -> filterChildTags(tag, withRemoved))
                .map(this::toDto)
                .sorted(Comparator.comparing(PomodoroTagDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addChildTagForParentTag(String parentTagName, String childTagName) {
        PomodoroTag parentTag = tagRepository.findByName(parentTagName).orElse(null);
        PomodoroTag childTag = tagRepository.findByName(childTagName).orElse(null);

        if (parentTag == null || childTag == null) {
            throw new TagOperationException("Unable to add child tag to parent tag due to absent one of the tag in database");
        }

        parentTag.addChildTag(childTag);
    }

    @Override
    @Transactional
    public void removeTag(String tagName) {
        PomodoroTag tag = tagRepository.findByName(tagName).orElse(null);

        if (tag == null) {
            throw new TagOperationException(String.format("Tag with name %s does not exist", tagName));
        }

        if (pomodoroRepository.existsByTagName(tagName)) {
            tag.setRemoved(true);
            tagRepository.save(tag);
        } else {
            if (tag.getParent() != null) {
                PomodoroTag parentTag = tag.getParent();
                parentTag.removeChild(tag);
                tagRepository.save(parentTag);
            } else {
                tagRepository.delete(tag);
            }
        }
    }

    @Override
    public void removeAllTags() {
        tagRepository.deleteAll();
        tagRepository.flush();
    }

    @Override
    public List<PomodoroTag> save(List<PomodoroTagDto> tags) {
        List<PomodoroTag> tagsToSave = toEntities(tags);

        return tagRepository.saveAll(tagsToSave);
    }

    @Override
    public boolean exists(String tagName) {
        return tagRepository.existsByName(tagName);
    }

    @Override
    public boolean isRelative(String tagToCheck, String tag) {
        PomodoroTag parentTag = tagRepository.findByName(tag).orElse(null);

        if (parentTag != null) {
            return parentTag.isRelative(tagToCheck);
        }

        return false;
    }

    private PomodoroTag filterChildTags(PomodoroTag parentTag, boolean withRemoved) {
        List<PomodoroTag> filteredChildTags = parentTag.getChildren().stream()
                .filter(tag -> filterTag(tag, withRemoved))
                .collect(Collectors.toList());

        return new PomodoroTag(parentTag.getId(), parentTag.getName(), null, filteredChildTags, parentTag.isRemoved());
    }

    private boolean filterTag(PomodoroTag tag, boolean withRemoved) {
        if (withRemoved) {
            return true;
        }
        return !tag.isRemoved();
    }

    private List<PomodoroTagDto> toDto(List<PomodoroTag> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PomodoroTagDto toDto(PomodoroTag pomodoroTag) {
        List<PomodoroTagDto> childTags = pomodoroTag.getChildren().stream()
                .map(tag -> new PomodoroTagDto(tag.getName(), List.of(), tag.isRemoved()))
                .sorted(Comparator.comparing(PomodoroTagDto::getName))
                .collect(Collectors.toList());

        return new PomodoroTagDto(pomodoroTag.getName(), childTags, pomodoroTag.isRemoved());
    }

    private List<PomodoroTag> toEntities(List<PomodoroTagDto> dtos) {
        List<PomodoroTag> mappedTags = new ArrayList<>();
        for (PomodoroTagDto tagDto : dtos) {
            PomodoroTag mappedParentTag = mapToEntity(tagDto);

            List<PomodoroTagDto> childTagDtos = tagDto.getChildren();
            if (!CollectionUtils.isEmpty(childTagDtos)) {
                for (PomodoroTagDto childTagDto : childTagDtos) {
                    PomodoroTag mappedChildTag = mapToEntity(childTagDto);
                    mappedParentTag.addChildTag(mappedChildTag);
                }
            }
            mappedTags.add(mappedParentTag);
        }

        return mappedTags;
    }

    private PomodoroTag mapToEntity(PomodoroTagDto pomodoroTagDto) {
        return new PomodoroTag(null, pomodoroTagDto.getName(), null, null, pomodoroTagDto.isRemoved());
    }

}

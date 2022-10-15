package com.igorgorbunov3333.timer.service.tag.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.PomodoroTagRepository;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.mapper.TagMapper;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultTagService implements TagService {

    private final PomodoroTagRepository pomodoroTagRepository;
    private final PomodoroRepository pomodoroRepository;
    private final TagMapper tagMapper;

    @Override
    public String save(String name) {
        PomodoroTag tag = pomodoroTagRepository.findByName(name).orElse(null);

        if (tag != null) {
            if (tag.isRemoved()) {
                SimplePrinter.print(String.format("Tag with name %s already marked as deleted", name));
            } else {
                SimplePrinter.print("Tag with name %s already exists");
            }

            return null;
        }

        PomodoroTag tagToSave = new PomodoroTag(null, name, false);
        PomodoroTag savedTag = pomodoroTagRepository.save(tagToSave);

        return savedTag.getName();
    }

    //TODO: use mapper
    @Override
    @Transactional(readOnly = true)
    public List<PomodoroTagDto> getSortedTags(boolean includeRemoved) {
        return pomodoroTagRepository.findAll().stream()
                .filter(tag -> filterTag(tag, includeRemoved))
                .map(tagMapper::mapToDto)
                .sorted(Comparator.comparing(tag -> tag.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeTag(String tagName) {
        PomodoroTag tag = pomodoroTagRepository.findByName(tagName).orElse(null);

        if (tag == null) {
            SimplePrinter.print(String.format("Tag with name %s does not exist", tagName));
            return;
        }

        if (pomodoroRepository.existsByPomodoroTagGroupPomodoroTagsName(tagName)) {
            tag.setRemoved(true);
            pomodoroTagRepository.save(tag);
        } else {
            pomodoroTagRepository.deleteByName(tagName);
        }
    }

    @Override
    public List<PomodoroTag> save(List<PomodoroTagDto> tags) {
        List<PomodoroTag> tagsToSave = tagMapper.mapToEntities(tags);

        return pomodoroTagRepository.saveAll(tagsToSave);
    }

    @Override
    public boolean exists(String tagName) {
        return pomodoroTagRepository.existsByName(tagName);
    }

    private boolean filterTag(PomodoroTag tag, boolean withRemoved) {
        if (withRemoved) {
            return true;
        }

        return !tag.isRemoved();
    }

}

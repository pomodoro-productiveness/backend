package com.igorgorbunov3333.timer.service.tag.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.TagRepository;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
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

    private final TagRepository tagRepository;
    private final PomodoroRepository pomodoroRepository;
    private final TagMapper tagMapper;
    private final PrinterService printerService;

    @Override
    public String save(String name) {
        PomodoroTag tag = tagRepository.findByName(name).orElse(null);

        if (tag != null) {
            if (tag.isRemoved()) {
                printerService.print(String.format("Tag with name %s already marked as deleted", name));
            } else {
                printerService.print("Tag with name %s already exists");
            }

            return null;
        }

        PomodoroTag tagToSave = new PomodoroTag(null, name, false);
        PomodoroTag savedTag = tagRepository.save(tagToSave);

        return savedTag.getName();
    }

    //TODO: use mapper
    @Override
    @Transactional(readOnly = true)
    public List<PomodoroTagDto> getSortedTags(boolean includeRemoved) {
        return tagRepository.findAll().stream()
                .filter(tag -> filterTag(tag, includeRemoved))
                .map(tagMapper::mapToDto)
                .sorted(Comparator.comparing(PomodoroTagDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeTag(String tagName) {
        PomodoroTag tag = tagRepository.findByName(tagName).orElse(null);

        if (tag == null) {
            printerService.print(String.format("Tag with name %s does not exist", tagName));
            return;
        }

        if (pomodoroRepository.existsByTagsName(tagName)) {
            tag.setRemoved(true);
            tagRepository.save(tag);
        } else {
            tagRepository.deleteByName(tagName);
        }
    }

    @Override
    public void removeAllTags() {
        tagRepository.deleteAll();
        tagRepository.flush();
    }

    @Override
    public List<PomodoroTag> save(List<PomodoroTagDto> tags) {
        List<PomodoroTag> tagsToSave = tagMapper.mapToEntities(tags);

        return tagRepository.saveAll(tagsToSave);
    }

    @Override
    public boolean exists(String tagName) {
        return tagRepository.existsByName(tagName);
    }

    private boolean filterTag(PomodoroTag tag, boolean withRemoved) {
        if (withRemoved) {
            return true;
        }

        return !tag.isRemoved();
    }

}

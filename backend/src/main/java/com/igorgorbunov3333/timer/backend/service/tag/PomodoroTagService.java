package com.igorgorbunov3333.timer.backend.service.tag;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagRepository;
import com.igorgorbunov3333.timer.backend.service.exception.EntityAlreadyExists;
import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroTagMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class PomodoroTagService {

    private static final String EXCEPTION_MESSAGE_NAME_BLANK = "Name of the tag must not be blank";

    private final PomodoroTagRepository pomodoroTagRepository;
    private final PomodoroTagMapper pomodoroTagMapper;

    public PomodoroTagDto getPomodoroTag(String tagName) {
        PomodoroTag pomodoroTag = pomodoroTagRepository.getByName(tagName);

        return pomodoroTagMapper.toDto(pomodoroTag);
    }

    public PomodoroTagDto save(String tagName) {
        if (StringUtils.isBlank(tagName)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_NAME_BLANK);
        }

        boolean tagExists = pomodoroTagRepository.existsByName(tagName);

        if (tagExists) {
            throw new EntityAlreadyExists(String.format("Tag with name [%s] is already exists", tagName));
        }

        PomodoroTag tagToSave = new PomodoroTag(null, tagName, false);
        PomodoroTag savedTag = pomodoroTagRepository.save(tagToSave);

        return pomodoroTagMapper.toDto(savedTag);
    }

    public List<PomodoroTagDto> getSortedTags(boolean includeRemoved) {
        return pomodoroTagRepository.findAll().stream()
                .filter(tag -> filterTag(tag, includeRemoved))
                .map(pomodoroTagMapper::toDto)
                .sorted(Comparator.comparing(tag -> tag.getName().toLowerCase()))
                .toList();
    }

    public void delete(String tagName) {
        if (StringUtils.isBlank(tagName)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_NAME_BLANK);
        }

        boolean tagExists = pomodoroTagRepository.existsByName(tagName);

        if (!tagExists) {
            throw new EntityDoesNotExist(String.format("%s with name [%s] does not exists", PomodoroTag.class.getSimpleName(), tagName));
        }

        pomodoroTagRepository.deleteByName(tagName);
    }

    private boolean filterTag(PomodoroTag tag, boolean withRemoved) {
        if (withRemoved) {
            return true;
        }

        return !tag.isRemoved();
    }

}

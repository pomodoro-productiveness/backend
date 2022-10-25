package com.igorgorbunov3333.timer.backend.service.tag;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;

import java.util.List;

public interface TagService {

    String save(String name);

    List<PomodoroTagDto> getSortedTags(boolean includeRemoved);

    void removeTag(String tagName);

    List<PomodoroTag> save(List<PomodoroTagDto> tags);

    boolean exists(String tagName);

}

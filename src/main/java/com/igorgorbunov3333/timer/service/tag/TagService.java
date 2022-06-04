package com.igorgorbunov3333.timer.service.tag;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.PomodoroTag;

import java.util.List;

public interface TagService {

    String save(String name);

    List<PomodoroTagDto> getSortedTags(boolean withRemoved);

    void addChildTagForParentTag(String parentPomodoroTag, String childPomodoroTag);

    void removeTag(String tagName);

    void removeAllTags();

    List<PomodoroTag> save(List<PomodoroTagDto> tags);
}

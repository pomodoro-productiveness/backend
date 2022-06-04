package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.tag.TagService;

import java.util.LinkedList;
import java.util.List;

public interface TagsProvidable {

    TagService getTagService();

    default List<PomodoroTagInfo> provideTags() {
        List<PomodoroTagDto> pomodoroTagDtos = getTagService().getSortedTags(false);

        return getTagsWithNumbers(pomodoroTagDtos);
    }

    private List<PomodoroTagInfo> getTagsWithNumbers(List<PomodoroTagDto> pomodoroTagDtos) {
        List<PomodoroTagInfo> tagPositionToTags = new LinkedList<>();
        int counter = 0;
        for (PomodoroTagDto tag : pomodoroTagDtos) {
            tagPositionToTags.add(new PomodoroTagInfo(++counter, tag.getName(), false));

            if (!tag.getChildren().isEmpty()) {
                for (PomodoroTagDto childTag : tag.getChildren()) {
                    tagPositionToTags.add(new PomodoroTagInfo(++counter, childTag.getName(), true));
                }
            }
        }
        return tagPositionToTags;
    }

}

package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.tag.TagService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public interface TagsWithNestingAndNumberingProvidable { //TODO: redo as separate service and use NumberToItemBuilder and ListOfItemsPrinter

    TagService getTagService();

    default List<PomodoroTagInfo> provideTags() {
        List<PomodoroTagDto> pomodoroTagDto = getTagService().getSortedTags(false);

        if (CollectionUtils.isEmpty(pomodoroTagDto)) {
            return List.of();
        }

        List<PomodoroTagInfo> numberedTags = new ArrayList<>();

        int count = 0;
        for (PomodoroTagDto tag : pomodoroTagDto) {
            numberedTags.add(new PomodoroTagInfo(++count, tag.getName()));
        }

        return numberedTags;
    }

}

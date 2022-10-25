package com.igorgorbunov3333.timer.backend.service.tag.provider;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.tag.TagService;
import com.igorgorbunov3333.timer.backend.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TagProvider {

    private final TagService tagService;

    public Map<Integer, PomodoroTagDto> provide() {
        List<PomodoroTagDto> pomodoroTags = tagService.getSortedTags(false);

        if (CollectionUtils.isEmpty(pomodoroTags)) {
            return Map.of();
        }

        return NumberToItemBuilder.build(pomodoroTags);
    }

}

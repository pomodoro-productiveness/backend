package com.igorgorbunov3333.timer.console.service.provider;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.service.tag.PomodoroTagComponent;
import com.igorgorbunov3333.timer.console.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TagProvider {

    private final PomodoroTagComponent pomodoroTagComponent;

    public Map<Integer, PomodoroTagDto> provide() {
        List<PomodoroTagDto> pomodoroTags = pomodoroTagComponent.getAllTags();

        if (CollectionUtils.isEmpty(pomodoroTags)) {
            return Map.of();
        }

        return NumberToItemBuilder.build(pomodoroTags);
    }

}
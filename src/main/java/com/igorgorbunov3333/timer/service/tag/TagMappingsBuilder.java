package com.igorgorbunov3333.timer.service.tag;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TagMappingsBuilder {

    public Map<String, Set<String>> buildTagMappings(List<PomodoroDto> pomodoro) {
        Map<String, Set<String>> tagMappings = new HashMap<>();

        for (PomodoroDto singlePomodoro : pomodoro) {
            Set<String> currentTags = singlePomodoro.getTags().stream()
                    .map(PomodoroTagDto::getName)
                    .collect(Collectors.toSet());

            fillTagMappings(tagMappings, currentTags);
        }

        return tagMappings;
    }

    private void fillTagMappings(Map<String, Set<String>> tagMappings, Set<String> currentTags) {
        for (String currentTagName : currentTags) {
            tagMappings.computeIfAbsent(currentTagName, v -> new HashSet<>());

            Set<String> otherTagsInPomodoro = new HashSet<>(currentTags);
            otherTagsInPomodoro.remove(currentTagName);

            Set<String> currentTagMappings = tagMappings.get(currentTagName);
            currentTagMappings.addAll(otherTagsInPomodoro);

            tagMappings.put(currentTagName, currentTagMappings);
        }
    }

}

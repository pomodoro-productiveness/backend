package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.repository.PomodoroTagRepository;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TestCommandProcessor implements CommandProcessor {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final PomodoroTagRepository pomodoroTagRepository;

    @Override
    public void process() {
        List<Pomodoro> pomodoroList =
                pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(
                        ZonedDateTime.of(LocalDateTime.of(2022, 1, 1, 0, 0), ZoneId.systemDefault()),
                        ZonedDateTime.now());

        Set<Set<String>> uniqueTagCombinations = new HashSet<>();
        for (Pomodoro p : pomodoroList) {
            Set<String> currentTags = p.getTags().stream()
                    .map(PomodoroTag::getName)
                    .collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(currentTags)) uniqueTagCombinations.add(currentTags);
        }

        Set<Set<String>> existingTagCombinations = pomodoroTagGroupRepository.findAll().stream()
                .map(group -> group.getPomodoroTags().stream().map(PomodoroTag::getName).collect(Collectors.toSet()))
                .collect(Collectors.toSet());

        Map<String, PomodoroTag> tagsMap = pomodoroTagRepository.findAll().stream()
                .collect(Collectors.toMap(PomodoroTag::getName, Function.identity()));

        List<PomodoroTagGroup> tagGroupsToSave = new ArrayList<>();
        for (Set<String> tagCombination : uniqueTagCombinations) {

            if (!existingTagCombinations.contains(tagCombination)) {
                List<PomodoroTag> tags = tagCombination.stream()
                        .map(tagsMap::get)
                        .collect(Collectors.toList());

                PomodoroTagGroup newPomodoroTagGroup = new PomodoroTagGroup(null, tags, 1L);
                tagGroupsToSave.add(newPomodoroTagGroup);
            }
        }

        pomodoroTagGroupRepository.saveAll(tagGroupsToSave);
    }

    @Override
    public String command() {
        return "test";
    }

}

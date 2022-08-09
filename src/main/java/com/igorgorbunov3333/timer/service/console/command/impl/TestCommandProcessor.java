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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        Map<Set<String>, PomodoroTagGroup> tagGroupsMap = pomodoroTagGroupRepository.findAll().stream()
                .collect(Collectors.toMap(group -> group.getPomodoroTags().stream().map(PomodoroTag::getName).collect(Collectors.toSet()),
                        Function.identity()));

        for (Pomodoro p : pomodoroList) {
            Set<String> pomodoroTags = p.getTags().stream()
                    .map(PomodoroTag::getName)
                    .collect(Collectors.toSet());

            PomodoroTagGroup correspondingTagGroup = tagGroupsMap.get(pomodoroTags);

            p.setPomodoroTagGroup(correspondingTagGroup);
        }

        pomodoroRepository.saveAll(pomodoroList);
    }

    @Override
    public String command() {
        return "test";
    }

}

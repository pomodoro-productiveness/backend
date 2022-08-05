package com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.group.PomodoroTagGroupService;
import com.igorgorbunov3333.timer.service.tag.provider.TagsFromUserProvider;
import com.igorgorbunov3333.timer.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagGroupUpdaterSessionProcessor implements TagSessionProcessor, NumberProvidable {

    @Getter
    private final CommandProvider commandProvider;
    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final PomodoroTagGroupService pomodoroTagGroupService;
    private final TagsFromUserProvider tagsFromUserProvider;
    private final LocalPomodoroUpdater localPomodoroUpdater;

    @Override
    public void process(Map<Integer, PomodoroTagDto> tagPositionToTags) {
        SimplePrinter.print("Chose tag group to update:");

        List<PomodoroTagGroup> tagGroups = pomodoroTagGroupRepository.findAll();

        Map<Integer, PomodoroTagGroup> numberedPomodoroTagGroups = NumberToItemBuilder.build(tagGroups);

        ListOfItemsPrinter.print(
                numberedPomodoroTagGroups,
                group -> group.getPomodoroTags().stream()
                        .map(PomodoroTag::getName)
                        .collect(Collectors.toList()));

        PomodoroTagGroup chosenTagGroup;
        while (true) {
            int number = provideNumber();

            if (number < 1) {
                return;
            }

            chosenTagGroup = numberedPomodoroTagGroups.get(number);

            if (chosenTagGroup == null) {
                SimplePrinter.printIncorrectNumber(number);
            } else {
                break;
            }
        }

        Set<String> newTags = tagsFromUserProvider.provideTagsFromUser();
        Set<String> oldTags = chosenTagGroup.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .collect(Collectors.toSet());

        tryToUpdateTagGroup(chosenTagGroup, newTags);

        SimplePrinter.print("Tag group updated successfully!");

        localPomodoroUpdater.updatePomodoroWithTagsByNewTags(oldTags, newTags);
    }

    private void tryToUpdateTagGroup(PomodoroTagGroup group, Set<String> tags) {
        try {
            pomodoroTagGroupService.updateTagGroup(group, tags);
        } catch (Exception e) {
            SimplePrinter.print(e.getMessage());
        }
    }

    @Override
    public String action() {
        return "4";
    }

}

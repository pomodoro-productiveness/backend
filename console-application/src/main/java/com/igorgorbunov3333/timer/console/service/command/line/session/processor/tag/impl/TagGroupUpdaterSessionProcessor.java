package com.igorgorbunov3333.timer.console.service.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.rest.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.console.service.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.console.service.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.provider.TagsFromUserProvider;
import com.igorgorbunov3333.timer.console.service.tag.PomodoroTagComponent;
import com.igorgorbunov3333.timer.console.service.tag.group.PomodoroTagGroupComponent;
import com.igorgorbunov3333.timer.console.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagGroupUpdaterSessionProcessor implements TagSessionProcessor, NumberProvidable {

    @Getter
    private final CommandProvider commandProvider;
    private final PomodoroTagGroupComponent pomodoroTagGroupComponent;
    private final TagsFromUserProvider tagsFromUserProvider;
    private final PomodoroTagComponent pomodoroTagComponent;

    @Override
    public void process(Map<Integer, PomodoroTagDto> tagPositionToTags) {
        SimplePrinter.printParagraph();
        SimplePrinter.print("Chose tag group to update:");

        List<PomodoroTagGroupDto> tagGroups = pomodoroTagGroupComponent.getTagGroups();

        Map<Integer, PomodoroTagGroupDto> numberedPomodoroTagGroups = NumberToItemBuilder.build(tagGroups);

        ListOfItemsPrinter.print(
                numberedPomodoroTagGroups,
                group -> group.getPomodoroTags().stream()
                        .map(PomodoroTagDto::getName)
                        .collect(Collectors.toList()));

        SimplePrinter.printParagraph();

        PomodoroTagGroupDto chosenTagGroup;
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

        pomodoroTagGroupComponent.save(new HashSet<>(newTags));

        SimplePrinter.printParagraph();
        SimplePrinter.print("Pomodoro group successfully updated with the following tags: " + newTags);
        SimplePrinter.printParagraph();
    }

    @Override
    public String action() {
        return "4";
    }

}

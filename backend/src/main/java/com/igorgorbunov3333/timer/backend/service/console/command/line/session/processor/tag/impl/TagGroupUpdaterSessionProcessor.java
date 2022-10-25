package com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagGroupRepository;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagRepository;
import com.igorgorbunov3333.timer.backend.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.tag.provider.TagsFromUserProvider;
import com.igorgorbunov3333.timer.backend.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PomodoroTagGroupRepository pomodoroTagGroupRepository;
    private final TagsFromUserProvider tagsFromUserProvider;
    private final PomodoroTagRepository pomodoroTagRepository;

    @Override
    @Transactional
    public void process(Map<Integer, PomodoroTagDto> tagPositionToTags) {
        SimplePrinter.printParagraph();
        SimplePrinter.print("Chose tag group to update:");

        List<PomodoroTagGroup> tagGroups = pomodoroTagGroupRepository.findAll();

        Map<Integer, PomodoroTagGroup> numberedPomodoroTagGroups = NumberToItemBuilder.build(tagGroups);

        ListOfItemsPrinter.print(
                numberedPomodoroTagGroups,
                group -> group.getPomodoroTags().stream()
                        .map(PomodoroTag::getName)
                        .collect(Collectors.toList()));

        SimplePrinter.printParagraph();

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

        List<PomodoroTag> newPomodoroTags = pomodoroTagRepository.findByNameIn(newTags);

        chosenTagGroup.setPomodoroTags(new HashSet<>(newPomodoroTags));

        pomodoroTagGroupRepository.save(chosenTagGroup);

        SimplePrinter.printParagraph();
        SimplePrinter.print("Pomodoro group successfully updated with the following tags: " + newTags);
        SimplePrinter.printParagraph();
    }

    @Override
    public String action() {
        return "4";
    }

}

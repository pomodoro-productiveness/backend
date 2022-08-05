package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.service.console.command.line.provider.AbstractLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.impl.TagCreationSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.TagProvider;
import com.igorgorbunov3333.timer.service.tag.group.PomodoroTagGroupService;
import com.igorgorbunov3333.timer.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO: refactor
@Component
@AllArgsConstructor
public class TagPomodoroSessionMapper extends AbstractLineProvider implements NumberProvidable {

    private final TagProvider tagProvider;
    @Getter
    private final CommandProvider commandProvider;
    private final LocalPomodoroUpdater localPomodoroUpdater;
    private final PomodoroTagGroupService pomodoroTagGroupService;
    private final TagCreationSessionProcessor tagCreationSessionProcessor;

    public void addTagToPomodoro(List<Long> pomodoroId) {
        Set<String> tags = provideTags();

        localPomodoroUpdater.updatePomodoroWithTag(pomodoroId, tags);
    }

    private Set<String> provideTags() {
        List<PomodoroTagGroup> pomodoroTagGroups = pomodoroTagGroupService.getLatestTagGroups();

        if (CollectionUtils.isEmpty(pomodoroTagGroups)) {
            return chosenTagsByUser();
        }

        Map<Integer, PomodoroTagGroup> pomodoroTagGroupMap = NumberToItemBuilder.build(pomodoroTagGroups);

        Function<PomodoroTagGroup, List<String>> extractorFunction = group -> group.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .sorted()
                .collect(Collectors.toList());
        ListOfItemsPrinter.print(pomodoroTagGroupMap, extractorFunction);

        PomodoroTagGroup chosenTagGroup;
        while (true) {
            SimplePrinter.print("Please choose tags group by it's number or press \"e\" to map by other tags");
            int numberAnswer = provideNumber();

            if (numberAnswer != -1) {
                chosenTagGroup = pomodoroTagGroupMap.get(numberAnswer);

                if (chosenTagGroup == null) {
                    SimplePrinter.print(String.format("No such group with number [%d]", numberAnswer));
                } else {
                    break;
                }
            } else {
                return chosenTagsByUser();
            }
        }

        pomodoroTagGroupService.updateOrderNumber(chosenTagGroup, null);

        return chosenTagGroup.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> chosenTagsByUser() {
        Set<String> tags;
        while (true) {
            tags = getTagsFromUser();

            SimplePrinter.print("Following tags [" + tags + "] will be mapped to pomodoro. Do you confirm?");
            SimplePrinter.printYesNoQuestion();

            String answer = provideLine();

            if (answer.startsWith("y")) {
                break;
            }
        }

        pomodoroTagGroupService.saveTagGroup(tags);

        return tags;
    }

    private Set<String> getTagsFromUser() {
        Set<String> chosenTags = new HashSet<>();

        while (true) {
            SimplePrinter.print("Do you want to create new tag?");
            SimplePrinter.printYesNoQuestion();

            String answer = provideLine();

            if (answer.startsWith("y")) {
                tagCreationSessionProcessor.process(null);
            } else {
                break;
            }
        }

        Map<Integer, PomodoroTagDto> numberedTags = tagProvider.provide();

        while (true) {
            SimplePrinter.print("Choose tag to map to saved pomodoro or press \"e\" to finish");

            ListOfItemsPrinter.print(numberedTags, PomodoroTagDto::getName);

            int chosenNumber = provideNumber();

            if (chosenNumber < 1) {
                break;
            }

            PomodoroTagDto tagToMap = numberedTags.get(chosenNumber);
            if (tagToMap != null) {
                chosenTags.add(tagToMap.getName());
                numberedTags.remove(chosenNumber);
            } else {
                SimplePrinter.print(String.format("Incorrect number [%d]", chosenNumber));
            }
        }

        return chosenTags;
    }

}

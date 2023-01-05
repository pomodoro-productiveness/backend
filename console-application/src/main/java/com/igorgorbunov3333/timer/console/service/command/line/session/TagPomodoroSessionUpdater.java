package com.igorgorbunov3333.timer.console.service.command.line.session;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.rest.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.console.service.command.line.provider.BaseLineProvider;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.provider.TagsFromUserProvider;
import com.igorgorbunov3333.timer.console.service.tag.group.PomodoroTagGroupComponent;
import com.igorgorbunov3333.timer.console.service.util.NumberToItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TagPomodoroSessionUpdater implements NumberProvidable, BaseLineProvider {

    @Getter
    private final CommandProvider commandProvider;
    private final PomodoroComponent pomodoroComponent;
    private final PomodoroTagGroupComponent pomodoroTagGroupComponent;
    private final TagsFromUserProvider tagsFromUserProvider;

    public void addTagToPomodoro(List<Long> pomodoroIds) {
        Set<String> tags = provideTags();

        pomodoroComponent.updatePomodoroWithTag(pomodoroIds, tags);
    }

    private Set<String> provideTags() {
        List<PomodoroTagGroupDto> pomodoroTagGroups = pomodoroTagGroupComponent.getTagGroups();

        if (CollectionUtils.isEmpty(pomodoroTagGroups)) {
            return chosenTagsByUser();
        }

        Map<Integer, PomodoroTagGroupDto> pomodoroTagGroupMap = NumberToItemBuilder.build(pomodoroTagGroups);

        Function<PomodoroTagGroupDto, List<String>> extractorFunction = group -> group.getPomodoroTags().stream()
                .map(PomodoroTagDto::getName)
                .collect(Collectors.toList());
        ListOfItemsPrinter.print(pomodoroTagGroupMap, extractorFunction);

        PomodoroTagGroupDto chosenTagGroup;
        while (true) {
            SimplePrinter.printParagraph();
            SimplePrinter.print("Please choose tags group by it's number or press \"e\" to map by other tags");
            SimplePrinter.printParagraph();

            int numberAnswer = provideNumber();

            if (numberAnswer != -1) {
                chosenTagGroup = pomodoroTagGroupMap.get(numberAnswer);

                SimplePrinter.printParagraph();
                if (chosenTagGroup == null) {
                    SimplePrinter.print(String.format("No such group with number [%d]", numberAnswer)); //TODO: move to PrintUtil all messages related to incorrect number
                } else {
                    break;
                }
            } else {
                return chosenTagsByUser();
            }
        }

        pomodoroTagGroupComponent.updateOrderNumber(chosenTagGroup.getId());

        return chosenTagGroup.getPomodoroTags().stream()
                .map(PomodoroTagDto::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> chosenTagsByUser() {
        Set<String> tags;
        while (true) {
            tags = tagsFromUserProvider.provideTagsFromUser();

            SimplePrinter.printParagraph();

            SimplePrinter.print("Following tags [" + tags + "] will be mapped to pomodoro. Do you confirm?");
            SimplePrinter.printYesNoQuestion();
            SimplePrinter.printParagraph();

            String answer = provideLine();

            SimplePrinter.printParagraph();

            if (answer.startsWith("y")) {
                break;
            }
        }

        pomodoroTagGroupComponent.save(tags);

        return tags;
    }

}

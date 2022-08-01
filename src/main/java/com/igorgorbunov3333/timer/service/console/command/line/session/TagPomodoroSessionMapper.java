package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagBunch;
import com.igorgorbunov3333.timer.service.console.command.line.provider.AbstractLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.TagProvider;
import com.igorgorbunov3333.timer.service.tag.bunch.PomodoroTagBunchService;
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

@Component
@AllArgsConstructor
public class TagPomodoroSessionMapper extends AbstractLineProvider implements NumberProvidable {

    private final TagProvider tagProvider;
    @Getter
    private final CommandProvider commandProvider;
    private final LocalPomodoroUpdater localPomodoroUpdater;
    private final PomodoroTagBunchService pomodoroTagBunchService;

    public void addTagToPomodoro(List<Long> pomodoroId) {
        Set<String> tags = getChosenTagBunch();

        localPomodoroUpdater.updatePomodoroWithTag(pomodoroId, tags);
    }

    private Set<String> getChosenTagBunch() {
        List<PomodoroTagBunch> pomodoroTagBunches = pomodoroTagBunchService.getLatestTagBunches();

        if (CollectionUtils.isEmpty(pomodoroTagBunches)) {
            return getChosenTags();
        }

        Map<Integer, PomodoroTagBunch> pomodoroTagBunchMap = NumberToItemBuilder.build(pomodoroTagBunches);

        Function<PomodoroTagBunch, List<String>> extractorFunction = bunch -> bunch.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .sorted()
                .collect(Collectors.toList());
        ListOfItemsPrinter.print(pomodoroTagBunchMap, extractorFunction);

        PomodoroTagBunch chosenTagBunch;
        while (true) {
            SimplePrinter.print("Please choose tags bunch by it's number or press \"e\" to map by other tags");
            int numberAnswer = provideNumber();

            if (numberAnswer != -1) {
                chosenTagBunch = pomodoroTagBunchMap.get(numberAnswer);

                if (chosenTagBunch == null) {
                    SimplePrinter.print(String.format("No such bunch with number [%d]", numberAnswer));
                } else {
                    break;
                }
            } else {
                return getChosenTags();
            }
        }

        pomodoroTagBunchService.updateOrderNumber(chosenTagBunch, null);

        return chosenTagBunch.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> getChosenTags() {
        Map<Integer, PomodoroTagDto> numberedTags = tagProvider.provide();

        Set<String> tags;
        while (true) {
            tags = getTagsFromUser(numberedTags);

            SimplePrinter.print("Following tags [" + tags + "] will be mapped to pomodoro. Do you confirm?");
            SimplePrinter.printYesNoQuestion();

            String answer = provideLine();

            if (answer.startsWith("y")) {
                break;
            }
        }

        pomodoroTagBunchService.saveBunch(tags);

        return tags;
    }

    private Set<String> getTagsFromUser(Map<Integer, PomodoroTagDto> tags) {
        Set<String> chosenTags = new HashSet<>();

        while (true) {
            SimplePrinter.print("Choose tag to map to saved pomodoro or press \"e\" to finish");

            ListOfItemsPrinter.print(tags, PomodoroTagDto::getName);

            int chosenNumber = provideNumber();

            if (chosenNumber < 1) {
                break;
            }

            PomodoroTagDto tagToMap = tags.get(chosenNumber);
            if (tagToMap != null) {
                chosenTags.add(tagToMap.getName());
                tags.remove(chosenNumber);
            } else {
                SimplePrinter.print(String.format("Incorrect number [%d]", chosenNumber));
            }
        }

        return chosenTags;
    }

}

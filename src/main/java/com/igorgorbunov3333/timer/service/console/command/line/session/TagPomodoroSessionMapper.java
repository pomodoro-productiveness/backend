package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagBunch;
import com.igorgorbunov3333.timer.service.console.command.line.provider.AbstractLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.tag.bunch.PomodoroTagBunchService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TagPomodoroSessionMapper extends AbstractLineProvider implements TagsWithNestingAndNumberingProvidable, TagsPrintable, TagAnswerProvidable {

    @Getter
    private final TagService tagService;
    @Getter
    private final CommandProvider commandProvider;
    private final LocalPomodoroUpdater localPomodoroUpdater;
    private final PomodoroTagBunchService pomodoroTagBunchService;
    private final ListOfItemsPrinter listOfItemsPrinter;

    public void addTagToPomodoro(List<Long> pomodoroId) {
        Set<String> tags = getTagsFromBunch();

        localPomodoroUpdater.updatePomodoroWithTag(pomodoroId, tags);
    }

    private Set<String> getTagsFromBunch() {
        List<PomodoroTagBunch> pomodoroTagBunches = pomodoroTagBunchService.getLatestTagBunches();

        if (CollectionUtils.isEmpty(pomodoroTagBunches)) {
            return getChosenTags();
        }

        Map<Integer, PomodoroTagBunch> pomodoroTagBunchMap = new HashMap<>();
        PomodoroTagBunch chosenTagBunch;

        Function<PomodoroTagBunch, List<String>> extractorFunction = bunch -> bunch.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .sorted()
                .collect(Collectors.toList());

        int count = 0;
        for (PomodoroTagBunch bunch : pomodoroTagBunches) {
            pomodoroTagBunchMap.put(++count, bunch);
        }

        listOfItemsPrinter.print(pomodoroTagBunchMap, extractorFunction);

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
        return chosenTagBunch.getPomodoroTags().stream()
                .map(PomodoroTag::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> getChosenTags() {
        List<PomodoroTagInfo> tagInfos = provideTags();

        Set<String> tags;
        while (true) {
            tags = getTagsFromUser(tagInfos);

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

    private Set<String> getTagsFromUser(List<PomodoroTagInfo> tagInfos) {
        List<PomodoroTagInfo> tagInfosCopy = new ArrayList<>(tagInfos);

        Set<String> tags = new HashSet<>();
        while (true) {
            SimplePrinter.print("Choose tag to map to saved pomodoro or press \"e\" to finish");
            printTags(tagInfosCopy);

            PomodoroTagInfo tagToMap = provideTagAnswer(tagInfos, null);
            if (tagToMap == null) {
                break;
            }

            tags.add(tagToMap.getTagName());
            tagInfosCopy.remove(tagToMap);
        }

        return tags;
    }

}

package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagBunch;
import com.igorgorbunov3333.timer.service.console.command.line.provider.AbstractLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.tag.bunch.PomodoroTagBunchService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TagPomodoroSessionMapper extends AbstractLineProvider implements TagsWithNestingAndNumberingProvidable, TagsPrintable, TagAnswerProvidable {

    private final LocalPomodoroUpdater localPomodoroUpdater;
    @Getter
    private final TagService tagService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;

    private final PomodoroTagBunchService pomodoroTagBunchService;

    public void addTagToPomodoro(Long pomodoroId) {
        printerService.print("Use latest tag bunches?");
        printerService.print("Yes (y), No");

        Set<String> tags;
        String answer = provideLine();

        if (answer.startsWith("y")) { //TODO: extract to common code
            tags = getTagsFromBunch();

            if (tags == null) {
                tags = getChosenTags();
            }
        } else {
            tags = getChosenTags();
        }

        localPomodoroUpdater.updatePomodoroWithTag(pomodoroId, tags);
    }

    private Set<String> getTagsFromBunch() {
        List<PomodoroTagBunch> pomodoroTagBunches = pomodoroTagBunchService.getLatestTagBunches();

        if (CollectionUtils.isEmpty(pomodoroTagBunches)) {
            printerService.print("There are no tag bunches yet");

            return null;
        }

        Map<Integer, PomodoroTagBunch> pomodoroTagBunchMap = new HashMap<>();
        PomodoroTagBunch chosenTagBunch;
        for (int i = 1; i <= pomodoroTagBunches.size(); i++) {
            PomodoroTagBunch currentBunch = pomodoroTagBunches.get(i - 1);
            pomodoroTagBunchMap.put(i, currentBunch);
            printerService.print(i + DefaultPrinterService.DOT + StringUtils.SPACE
                    + currentBunch.getPomodoroTags().stream()
                    .map(PomodoroTag::getName)
                    .sorted()
                    .collect(Collectors.toList()));
        }

        while (true) {
            printerService.print("Please choose tags bunch by it's number");
            int numberAnswer = provideNumber();

            if (numberAnswer != -1) {
                chosenTagBunch = pomodoroTagBunchMap.get(numberAnswer);

                if (chosenTagBunch == null) {
                    printerService.print(String.format("No such bunch with number [%d]", numberAnswer));
                } else {
                    break;
                }
            } else {
                return null;
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

            printerService.print("Following tags [" + tags + "] will be mapped to pomodoro. Do you confirm?");
            printerService.print("Yes (y), No");

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
            printerService.print("Choose tag to map to saved pomodoro or press \"e\" to finish");
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

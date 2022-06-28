package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.AbstractLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void mapTagToPomodoro(Long pomodoroId) {
        List<PomodoroTagInfo> tagInfos = provideTags();

        Set<String> tags = new HashSet<>();
        while (true) {
            printerService.print("Choose tag to map to saved pomodoro or press \"e\" to finish");
            printTags(tagInfos);

            PomodoroTagInfo tagToMap = provideTagAnswer(tagInfos, null);
            if (tagToMap == null) {
                break;
            }

            tags.add(tagToMap.getTagName());
            tagInfos.remove(tagToMap);
        }

        printerService.print("Following tags [" + tags + "] will be mapped to pomodoro. Do you confirm?");
        printerService.print("Yes (y), No");

        String answer = provideLine();

        if (answer.startsWith("y")) {
            localPomodoroUpdater.updatePomodoroWithTag(pomodoroId, tags);
        }

    }

}

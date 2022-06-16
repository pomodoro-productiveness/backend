package com.igorgorbunov3333.timer.service.console.command.line.session.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagAnswerProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagSessionProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagsProvidable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.updater.LocalPomodoroUpdater;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: refactor
@Service
@AllArgsConstructor
public class TagRemappingSessionProcessor implements TagSessionProcessor, TagAnswerProvidable, TagsProvidable {

    private final CurrentDayLocalPomodoroProvider currentDayLocalPomodoroProvider;
    private final LocalPomodoroUpdater pomodoroUpdater;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;
    @Getter
    private final TagService tagService;

    @Override
    public void process(List<PomodoroTagInfo> tagPositionToTags) {
        printerService.print("Choose pomodoro by it's number to remap it's tag");
        printerService.printParagraph();

        List<PomodoroDto> dailyPomodoro = currentDayLocalPomodoroProvider.provide(null);

        Map<Integer, PomodoroDto> numeratedPomodoro = new HashMap<>();
        int count = 0;
        for (PomodoroDto pomodoroDto : dailyPomodoro) {
            numeratedPomodoro.put(++count, pomodoroDto);
        }

        printPomodoro(numeratedPomodoro);

        printerService.printParagraph();

        PomodoroDto chosenPomodoro = providePomodoro(numeratedPomodoro);
        if (chosenPomodoro == null) {
            return;
        }

        printerService.print("Chose tag to map to this pomodoro");

        List<PomodoroTagInfo> tags = provideTags();
        printTags(tags);

        PomodoroTagInfo chosenTag = provideTagAnswer(tags, null);
        if (chosenTag == null) {
            return;
        }

        pomodoroUpdater.updatePomodoroWithTag(chosenPomodoro.getId(), chosenTag.getTagName());

        printerService.print("Tag remapped successfully for pomodoro");
    }

    private PomodoroDto providePomodoro(Map<Integer, PomodoroDto> numeratedPomodoro) {
        while (true) {
            Integer pomodoroNumber = provideNumber();
            if (pomodoroNumber == null) {
                continue;
            }

            if (pomodoroNumber.equals(-1)) {
                return null;
            }

            PomodoroDto chosenPomodoro = numeratedPomodoro.get(pomodoroNumber);

            if (chosenPomodoro == null) {
                printerService.print(String.format("No pomodoro with number %d", pomodoroNumber));
                printerService.print("Chose number or press \"e\" to exit menu");
                continue;
            }

            return chosenPomodoro;
        }
    }

    private void printPomodoro(Map<Integer, PomodoroDto> numeratedPomodoro) {
        for (Map.Entry<Integer, PomodoroDto> entry : numeratedPomodoro.entrySet()) {
            PomodoroDto currentPomodoro = entry.getValue();
            printerService.printWithCarriage(entry.getKey() + DefaultPrinterService.DOT + StringUtils.SPACE);
            printerService.printPomodoro(currentPomodoro, true, entry.getKey() % 2 != 0);
        }
    }

    @Override
    public String action() {
        return "4";
    }

}

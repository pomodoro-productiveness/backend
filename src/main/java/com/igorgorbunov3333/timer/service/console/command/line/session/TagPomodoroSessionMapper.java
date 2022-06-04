package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TagPomodoroSessionMapper implements TagsProvidable, TagsPrintable, TagAnswerProvider {

    private final PomodoroService pomodoroService;
    @Getter
    private final TagService tagService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;

    public void mapTagToPomodoro(Long pomodoroId) {
        List<PomodoroTagInfo> tags = provideTags();

        printerService.print("Choose tag to map to saved pomodoro or press \"e\" to exit");
        printTags(tags);

        PomodoroTagInfo tagToMap = getTagAnswer(tags, null);
        if (tagToMap == null) {
            return;
        }

        pomodoroService.updatePomodoroWithTag(pomodoroId, tagToMap.getTagName());
    }

}
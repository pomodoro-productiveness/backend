package com.igorgorbunov3333.timer.service.console.command.line.session.impl;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagAnswerProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TagRemovingSessionProcessor implements TagSessionProcessor, TagAnswerProvider {

    private final TagService tagService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(List<PomodoroTagInfo> tags) {
        printerService.print("Enter the tag number to remove tag or press \"e\" to exit");

        PomodoroTagInfo parentTag = getTagAnswer(tags, null);
        if (parentTag == null) {
            return;
        }

        tagService.removeTag(parentTag.getTagName());

        printerService.print("Pomodoro tag successfully removed");
    }

    @Override
    public String action() {
        return "2";
    }

}

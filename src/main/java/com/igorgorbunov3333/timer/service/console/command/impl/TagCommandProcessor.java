package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagCommandSessionStarter;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagsPrintable;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagsWithNestingAndNumberingProvidable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagCommandProcessor implements CommandProcessor, TagsWithNestingAndNumberingProvidable, TagsPrintable {

    private final TagCommandSessionStarter tagCommandSessionStarter;
    @Getter
    private final TagService tagService;
    @Getter
    private final PrinterService printerService;

    @Override
    public void process() {
        boolean sessionFinished = false;

        List<PomodoroTagInfo> tagsWithNumbers = provideTags();
        printTags(tagsWithNumbers);

        while (!sessionFinished) {
            sessionFinished = tagCommandSessionStarter.startSession(tagsWithNumbers);
        }
    }

    @Override
    public String command() {
        return "tag";
    }

}

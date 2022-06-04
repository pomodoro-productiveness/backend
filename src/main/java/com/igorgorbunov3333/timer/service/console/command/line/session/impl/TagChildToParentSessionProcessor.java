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

import java.util.List;

@Service
@AllArgsConstructor
public class TagChildToParentSessionProcessor implements TagSessionProcessor, TagAnswerProvider {

    private final TagService tagService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;

    @Override
    public void process(List<PomodoroTagInfo> tags) {
        printerService.print("Enter the tag number that should be the parent or press \"e\" to exit");

        PomodoroTagInfo parentTag = getTagAnswer(tags, null);
        if (parentTag == null) {
            return;
        }

        printerService.print("Choose a tags number to make it child to chosen parent tag or press \"e\" to exit");

        PomodoroTagInfo childTag = getTagAnswer(tags, parentTag.getTagName());
        if (childTag == null) {
            return;
        }

        if (childTag.getTagName().equals(parentTag.getTagName())) {
            printerService.print(String.format("Tag %s cannot be parent and child tag at the same time", childTag.getTagName()));
        } else {
            tryToAddChildTagToParentTag(parentTag.getTagName(), childTag.getTagName());
        }
    }

    private void tryToAddChildTagToParentTag(String parentTag, String childTag) {
        try {
            tagService.addChildTagForParentTag(parentTag, childTag);
        } catch (Exception e) {
            printerService.print("Unable to add child tag to parent tag due to the error:");
            e.printStackTrace();
        }
    }

    @Override
    public String action() {
        return "3";
    }

}

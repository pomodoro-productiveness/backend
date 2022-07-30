package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;

import java.util.List;

public interface TagAnswerProvidable extends TagsPrintable, NumberProvidable {

    default PomodoroTagInfo provideTagAnswer(List<PomodoroTagInfo> tags, String parentTagName) {
        do {
            Integer intAnswerNumber = provideNumber(); //TODO: int answer can not be null, fix it
            if (intAnswerNumber == null) {
                printTags(tags);
                continue;
            }
            if (intAnswerNumber.equals(-1)) {
                return null;
            }

            PomodoroTagInfo tag = tags.stream()
                    .filter(tagDto -> tagDto.getTagNumber() == intAnswerNumber)
                    .findFirst()
                    .orElse(null);

            if (tag == null) {
                SimplePrinter.print(String.format("No tag with number %d. If you want exit then press \"e\"", intAnswerNumber));
                printTags(tags);
            } else if (tag.getTagName().equals(parentTagName)) { //TODO: move to service where it used
                SimplePrinter.print(String.format("Tag %s cannot be parent and child tag at the same time", tag.getTagName()));
            } else {
                return tag;
            }
        } while (true);

    }

}

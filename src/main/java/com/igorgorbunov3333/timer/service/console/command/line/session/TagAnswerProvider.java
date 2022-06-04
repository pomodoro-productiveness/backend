package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;

import java.util.List;

public interface TagAnswerProvider extends TagsPrintable {

    CommandProvider getCommandProvider();

    PrinterService getPrinterService();

    default PomodoroTagInfo getTagAnswer(List<PomodoroTagInfo> tags, String parentTagName) {
        do {
            try {
                String tagNumberAnswer = getCommandProvider().provideLine();
                if (tagNumberAnswer.toLowerCase().startsWith("e")) {
                    return null;
                }

                int intAnswerNumber = Integer.parseInt(tagNumberAnswer);

                PomodoroTagInfo tag = tags.stream()
                        .filter(tagDto -> tagDto.getTagNumber() == intAnswerNumber)
                        .findFirst()
                        .orElse(null);

                if (tag == null) {
                    getPrinterService().print(String.format("No tag with number %s. If you want exit then press \"e\"", tagNumberAnswer));
                    printTags(tags);
                } else if (tag.getTagName().equals(parentTagName)) {
                    getPrinterService().print(String.format("Tag %s cannot be parent and child tag at the same time", tag.getTagName()));
                } else {
                    return tag;
                }
            } catch (NumberFormatException e) {
                getPrinterService().print("Incorrect format, please enter a number of the tag again or press \"e\" to exit");
                printTags(tags);
            }
        } while (true);
    }

}

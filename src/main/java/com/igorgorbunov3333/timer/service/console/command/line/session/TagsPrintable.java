package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;

import java.util.List;

public interface TagsPrintable {

    PrinterService getPrinterService();

    default void printTags(List<PomodoroTagInfo> tagsWithNumbers) {
        for (PomodoroTagInfo tag : tagsWithNumbers) {
            if (!tag.isChildTag()) {
                getPrinterService().print(tag.getTagNumber() + DefaultPrinterService.DOT + DefaultPrinterService.SPACE + tag.getTagName());
            } else {
                getPrinterService().print(DefaultPrinterService.TABULATION + tag.getTagNumber() + DefaultPrinterService.DOT + tag.getTagName());
            }
        }
        if (!tagsWithNumbers.isEmpty()) {
            getPrinterService().printParagraph();
        }
    }

}

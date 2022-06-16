package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface TagsPrintable {

    PrinterService getPrinterService();

    default void printTags(List<PomodoroTagInfo> tags) {
        getPrinterService().printParagraph();

        getPrinterService().print("Current tags:");

        getPrinterService().printParagraph();

        for (PomodoroTagInfo tag : tags) {
            if (!tag.isChildTag()) {
                getPrinterService().print(tag.getTagNumber() + DefaultPrinterService.DOT + StringUtils.SPACE + tag.getTagName());
            } else {
                getPrinterService().print(DefaultPrinterService.TABULATION + tag.getTagNumber() + DefaultPrinterService.DOT + tag.getTagName());
            }
        }
        if (!tags.isEmpty()) {
            getPrinterService().printParagraph();
        }
    }

}

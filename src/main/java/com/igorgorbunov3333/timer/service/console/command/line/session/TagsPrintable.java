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

        tags.forEach(t -> getPrinterService().print(t.getTagNumber() + DefaultPrinterService.DOT + StringUtils.SPACE + t.getTagName()));

        if (!tags.isEmpty()) {
            getPrinterService().printParagraph();
        }
    }

}

package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface TagsPrintable {

    default void printTags(List<PomodoroTagInfo> tags) {
        SimplePrinter.printParagraph();

        SimplePrinter.print("Current tags:");

        tags.forEach(t -> SimplePrinter.print(t.getTagNumber() + PrintUtil.DOT + StringUtils.SPACE + t.getTagName()));

        if (!tags.isEmpty()) {
            SimplePrinter.printParagraph();
        }
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandService;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagCommandService implements CommandService {

    private final PrinterService printerService;
    private final CommandProvider commandProvider;
    private final TagService tagService;

    @Override
    public void process() {
        printerService.print("Provide tag name");

        String tagName = commandProvider.provideLine();

        tagService.saveTag(tagName);

        printerService.print("Tag successfully saved");
    }

    @Override
    public String command() {
        return "tag";
    }

}

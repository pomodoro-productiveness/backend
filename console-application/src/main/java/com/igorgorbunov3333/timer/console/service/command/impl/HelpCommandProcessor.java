package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HelpCommandProcessor implements CommandProcessor {

    private final PrinterService printerService;

    @Override
    public void process() {
        printerService.printFeaturesList();
    }

    @Override
    public String command() {
        return "help";
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
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

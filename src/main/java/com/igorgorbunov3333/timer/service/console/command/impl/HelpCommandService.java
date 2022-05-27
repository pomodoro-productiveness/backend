package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandService;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HelpCommandService implements CommandService {

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

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.synchronization.priority.local.LocalPrioritySynchronizationToggler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExitCommandProcessor implements CommandProcessor {

    private final LocalPrioritySynchronizationToggler localPrioritySynchronizationToggler;
    private final PrinterService printerService;

    @Override
    @SneakyThrows
    public void process() {
        if (!localPrioritySynchronizationToggler.needToSynchronize()) {
            printerService.print("Closing the application");
            Thread.sleep(1000);
            System.exit(0);
        } else {
            printerService.print("Unable to close the application now due to not finished synchronization. "
                    + "Please try again later");
        }
    }

    @Override
    public String command() {
        return "e";
    }

}

package com.igorgorbunov3333.timer.service.console.command.line;

import com.igorgorbunov3333.timer.service.console.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.processor.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@AllArgsConstructor
public class CommandLineController {

    private static final String INVALID_INPUT = "Invalid input, please try again";

    private final PrinterService printerService;
    private final CommandProcessor commandProcessor;
    private final CommandProvider commandProvider;

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        printerService.printFeaturesList();
        while (true) {
            String command = commandProvider.provideLine();
            gotoChoice(command);
            if (command.equals("exit")) {
                break;
            }
        }
        sc.close();
    }

    private void gotoChoice(String command) {
        CurrentCommandStorage.currentCommand = command;
        boolean correctCommand = commandProcessor.process(command);

        if (!correctCommand) {
            printerService.print(INVALID_INPUT);
        }
    }

}

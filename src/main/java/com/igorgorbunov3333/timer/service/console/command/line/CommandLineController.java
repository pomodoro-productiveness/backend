package com.igorgorbunov3333.timer.service.console.command.line;

import com.igorgorbunov3333.timer.service.console.command.CommandCoordinator;
import com.igorgorbunov3333.timer.service.console.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@AllArgsConstructor
public class CommandLineController {

    private static final String INVALID_INPUT = "Invalid input, please try again";

    private final PrinterService printerService;
    private final CommandCoordinator commandCoordinator;
    private final CommandProvider commandProvider;

    public void start() {
        Scanner sc = new Scanner(System.in);
        SimplePrinter.printParagraph();
        printerService.printFeaturesList();
        while (true) {
            SimplePrinter.printParagraph();
            String command = commandProvider.provideLine();
            SimplePrinter.printParagraph();
            gotoChoice(command);
            if (command.equals("exit")) {
                break;
            }
        }
        sc.close();
    }

    private void gotoChoice(String command) {
        CurrentCommandStorage.currentCommand = command;
        boolean correctCommand = commandCoordinator.coordinate(command);

        if (!correctCommand) {
            SimplePrinter.print(INVALID_INPUT);
        }
    }

}

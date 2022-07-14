package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;

public interface NumberProvidable {

    CommandProvider getCommandProvider();

    PrinterService getPrinterService();

    default int provideNumber() {
        while (true) {
            try {
                String tagNumberAnswer = getCommandProvider().provideLine();
                if (tagNumberAnswer.toLowerCase().startsWith("e")) {
                    return -1;
                }

                int response = Integer.parseInt(tagNumberAnswer);

                if (response <= 0) {
                    getPrinterService().print("Number cannot be less then or equal to zero");
                } else {
                    return response;
                }
            } catch (NumberFormatException e) {
                getPrinterService().print("Incorrect format, please enter a number again or press \"e\" to exit");
            }
        }
    }

}

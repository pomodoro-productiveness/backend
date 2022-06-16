package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;

public interface NumberProvidable {

    CommandProvider getCommandProvider();

    PrinterService getPrinterService();

    default Integer provideNumber() {
        try {
            String tagNumberAnswer = getCommandProvider().provideLine();
            if (tagNumberAnswer.toLowerCase().startsWith("e")) {
                return -1;
            }

            return Integer.parseInt(tagNumberAnswer);

        } catch (NumberFormatException e) {
            getPrinterService().print("Incorrect format, please enter a number again or press \"e\" to exit");
            return null;
        }
    }

}

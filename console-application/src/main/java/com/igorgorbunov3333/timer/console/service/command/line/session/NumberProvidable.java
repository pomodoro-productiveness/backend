package com.igorgorbunov3333.timer.console.service.command.line.session;

import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;

public interface NumberProvidable {

    CommandProvider getCommandProvider();

    default int provideNumber() {
        while (true) {
            try {
                String tagNumberAnswer = getCommandProvider().provideLine();
                if (tagNumberAnswer.toLowerCase().startsWith("e")) {
                    return -1;
                }

                int response = Integer.parseInt(tagNumberAnswer);

                if (response <= 0) {
                   SimplePrinter.print("Number cannot be less then or equal to zero");
                } else {
                    return response;
                }
            } catch (NumberFormatException e) {
                SimplePrinter.printParagraph();
                SimplePrinter.print("Incorrect format, please enter a number again or press \"e\" to exit");
                SimplePrinter.printParagraph();
            }
        }
    }

}

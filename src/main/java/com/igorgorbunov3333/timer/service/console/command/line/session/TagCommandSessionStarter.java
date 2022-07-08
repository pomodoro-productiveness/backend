package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TagCommandSessionStarter {

    private final PrinterService printerService;
    private final CommandProvider commandProvider;
    private final TagCommandSessionCoordinator tagCommandSessionCoordinator;

    public boolean startSession(List<PomodoroTagInfo> tagsWithNumbers) {
        String answer;
        printerService.print("Chose an option:");
        printerService.print("Press 1 to create a new tag");
        printerService.print("Press 2 to remove a tag");
        printerService.print("Press 3 to remap tags for pomodoro");
        printerService.print("You may exit by pressing \"e\" to save current changes");

        answer = commandProvider.provideLine();
        if (answer.startsWith("e")) {
            printerService.print("Tag menu abandoned"); //TODO: message duplication
            return true;
        }

        boolean successfullyChosen = tagCommandSessionCoordinator.coordinate(answer, tagsWithNumbers);

        if (!successfullyChosen) {
            printerService.print("Incorrect option, please retry or press \"e\" to exit");
            return false;
        }

        return false;
    }

}

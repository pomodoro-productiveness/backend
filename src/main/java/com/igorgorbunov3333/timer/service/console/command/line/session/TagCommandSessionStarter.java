package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.coordinator.TagCommandSessionCoordinator;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TagCommandSessionStarter {

    private final CommandProvider commandProvider;
    private final TagCommandSessionCoordinator tagCommandSessionCoordinator;

    public boolean startSession(List<PomodoroTagInfo> tagsWithNumbers) {
        SimplePrinter.printParagraph();
        SimplePrinter.print("Chose an option:");
        SimplePrinter.print("Press 1 to create a new tag");
        SimplePrinter.print("Press 2 to remove a tag");
        SimplePrinter.print("Press 3 to remap tags for pomodoro");
        SimplePrinter.print("You may exit by pressing \"e\" to save current changes");

        String answer = commandProvider.provideLine();
        if (answer.startsWith("e")) {
            SimplePrinter.print("Tag menu abandoned"); //TODO: message duplication
            return true;
        }

        boolean successfullyChosen = tagCommandSessionCoordinator.coordinate(answer, tagsWithNumbers);

        if (!successfullyChosen) {
            SimplePrinter.print("Incorrect option, please retry or press \"e\" to exit");
            return false;
        }

        return false;
    }

}

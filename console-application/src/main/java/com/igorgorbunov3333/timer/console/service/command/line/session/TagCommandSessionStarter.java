package com.igorgorbunov3333.timer.console.service.command.line.session;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.coordinator.TagCommandSessionCoordinator;
import com.igorgorbunov3333.timer.console.service.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.provider.TagProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class TagCommandSessionStarter {

    private final CommandProvider commandProvider;
    private final TagCommandSessionCoordinator tagCommandSessionCoordinator;
    private final TagProvider tagProvider;

    public boolean startSession() {
        Map<Integer, PomodoroTagDto> tagsWithNumbers = tagProvider.provide();

        ListOfItemsPrinter.print(tagsWithNumbers, PomodoroTagDto::getName);

        SimplePrinter.printParagraph();
        SimplePrinter.print("Chose an action:");
        SimplePrinter.print("Press 1 to create a new tag");
        SimplePrinter.print("Press 2 to remove a tag");
        SimplePrinter.print("Press 3 to remap tags for pomodoro");
        SimplePrinter.print("Press 4 to update tag group");
        SimplePrinter.print("You may exit by pressing \"e\" to save current changes");
        SimplePrinter.printParagraph();

        String answer = commandProvider.provideLine();
        if (answer.startsWith("e")) {
            SimplePrinter.printParagraph();
            SimplePrinter.print(PrintUtil.LEFT_TAGS_MENU);
            return true;
        }

        boolean successfullyChosen = tagCommandSessionCoordinator.coordinate(answer, tagsWithNumbers);

        if (!successfullyChosen) {
            SimplePrinter.printParagraph();
            SimplePrinter.print("Incorrect option, please retry or press \"e\" to exit");
            return false;
        }

        return false;
    }

}

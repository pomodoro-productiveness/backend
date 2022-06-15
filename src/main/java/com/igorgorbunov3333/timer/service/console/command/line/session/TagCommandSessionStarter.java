package com.igorgorbunov3333.timer.service.console.command.line.session;

import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TagCommandSessionStarter implements TagsProvidable, TagsPrintable {

    @Getter
    private final TagService tagService;
    @Getter
    private final PrinterService printerService;
    private final CommandProvider commandProvider;
    private final TagCommandSessionCoordinator tagCommandSessionCoordinator;

    public boolean startSession() {
        List<PomodoroTagInfo> tagsWithNumbers = provideTags();
        printTags(tagsWithNumbers);

        boolean successfullyChosen;
        String answer;
        printerService.print("Chose an option:");
        printerService.print("Press 1 to create new tag");
        printerService.print("Press 2 to remove a tag");
        printerService.print("Press 3 to set the relationship of the child tag to the parent tag");
        printerService.print("You may exit by pressing \"e\" to save current changes");

        answer = commandProvider.provideLine();
        if (answer.startsWith("e")) {
            printerService.print("Tag menu abandoned"); //TODO: message duplication
            return true;
        }

        successfullyChosen = tagCommandSessionCoordinator.coordinate(answer, tagsWithNumbers);

        if (!successfullyChosen) {
            printerService.print("Incorrect option, please retry or press \"e\" to exit");
            return false;
        }

        return false;
    }

}

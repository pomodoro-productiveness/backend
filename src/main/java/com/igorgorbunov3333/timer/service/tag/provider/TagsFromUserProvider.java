package com.igorgorbunov3333.timer.service.tag.provider;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.AbstractLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.impl.TagCreationSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class TagsFromUserProvider extends AbstractLineProvider implements NumberProvidable {

    @Getter
    private final CommandProvider commandProvider;
    private final TagCreationSessionProcessor tagCreationSessionProcessor;
    private final TagProvider tagProvider;

    public Set<String> provideTagsFromUser() {
        Set<String> chosenTags = new HashSet<>();

        while (true) {
            SimplePrinter.print("Do you want to create new tag?");
            SimplePrinter.printYesNoQuestion();

            String answer = provideLine();

            if (answer.startsWith("y")) {
                tagCreationSessionProcessor.process(null);
            } else {
                break;
            }
        }

        Map<Integer, PomodoroTagDto> numberedTags = tagProvider.provide();

        while (true) {
            SimplePrinter.print("Choose tag or press \"e\" to finish");

            ListOfItemsPrinter.print(numberedTags, PomodoroTagDto::getName);

            int chosenNumber = provideNumber();

            if (chosenNumber < 1) {
                break;
            }

            PomodoroTagDto tagToMap = numberedTags.get(chosenNumber);
            if (tagToMap != null) {
                chosenTags.add(tagToMap.getName());
                numberedTags.remove(chosenNumber);
            } else {
                SimplePrinter.print(String.format("Incorrect number [%d]", chosenNumber));
            }
        }

        return chosenTags;
    }

}

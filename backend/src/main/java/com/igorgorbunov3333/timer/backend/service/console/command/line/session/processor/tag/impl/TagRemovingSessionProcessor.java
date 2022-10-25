package com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.console.command.line.provider.BaseLineProvider;
import com.igorgorbunov3333.timer.backend.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class TagRemovingSessionProcessor implements TagSessionProcessor, NumberProvidable, BaseLineProvider {

    private final TagService tagService;
    @Getter
    private final CommandProvider commandProvider;

    @Override
    public void process(Map<Integer, PomodoroTagDto> tags) {
        SimplePrinter.printParagraph();
        SimplePrinter.print("Enter the tag number to remove tag or press \"e\" to exit:");
        SimplePrinter.printParagraph();

        PomodoroTagDto chosenTag;
        while (true) {
            int chosenNumber = provideNumber();
            if (chosenNumber < 1) {
                return;
            }

            chosenTag = tags.get(chosenNumber);
            if (chosenTag == null) {
                SimplePrinter.printParagraph();
                SimplePrinter.print(String.format("No tag with number [%d]!", chosenNumber));
                SimplePrinter.printParagraph();
            } else {
                break;
            }
        }

        String tagNameToRemove = chosenTag.getName();

        SimplePrinter.printParagraph();
        SimplePrinter.print(String.format("Are you sure you want to remove tag with name [%s]?", tagNameToRemove));
        SimplePrinter.printYesNoQuestion();
        SimplePrinter.printParagraph();

        String answer = provideLine();

        SimplePrinter.printParagraph();

        if (answer.startsWith("y")) {
            tagService.removeTag(tagNameToRemove);
            SimplePrinter.print(String.format("Pomodoro tag [%s] successfully removed", tagNameToRemove));
        }
    }

    @Override
    public String action() {
        return "2";
    }

}

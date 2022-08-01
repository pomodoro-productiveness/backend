package com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class TagRemovingSessionProcessor implements TagSessionProcessor, NumberProvidable {

    private final TagService tagService;
    @Getter
    private final CommandProvider commandProvider;

    @Override
    public void process(Map<Integer, PomodoroTagDto> tags) {
        SimplePrinter.print("Enter the tag number to remove tag or press \"e\" to exit");

        PomodoroTagDto chosenTag;
        while (true) {
            int chosenNumber = provideNumber();
            if (chosenNumber < 1) {
                return;
            }

            chosenTag = tags.get(chosenNumber);
            if (chosenTag == null) {
                SimplePrinter.print(String.format("No tag with number [%d]", chosenNumber));
            } else {
                break;
            }
        }

        tagService.removeTag(chosenTag.getName());

        SimplePrinter.print("Pomodoro tag successfully removed");
    }

    @Override
    public String action() {
        return "2";
    }

}

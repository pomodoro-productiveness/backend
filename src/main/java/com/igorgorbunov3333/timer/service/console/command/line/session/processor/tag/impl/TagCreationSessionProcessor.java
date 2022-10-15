package com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.console.command.line.provider.BaseLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.exception.TagOperationException;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
public class TagCreationSessionProcessor implements TagSessionProcessor, BaseLineProvider {

    private static final int TAG_MAX_LENGTH = 30;

    private final TagService tagService;
    private final CommandProvider commandProvider;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) //TODO: remove?
    public void process(Map<Integer, PomodoroTagDto> tagsWithNumbers) {
        SimplePrinter.printParagraph();
        SimplePrinter.print("Please provide a tag name:");
        SimplePrinter.printParagraph();

        String tagNameAnswer = provideAndValidateTagName();
        if (tagNameAnswer == null) {
            return;
        }

        String savedTagName = null;
        try {
            savedTagName = tagService.save(tagNameAnswer);
        } catch (TagOperationException e) {
            SimplePrinter.print(e.getMessage());
        }

        if (savedTagName != null) {
            SimplePrinter.print(String.format("Tag with name [%s] successfully saved", savedTagName));
        }
    }

    //TODO: refactor
    private String provideAndValidateTagName() {
        do {
            String tagNameAnswer = commandProvider.provideLine();

            if (tagNameAnswer.equalsIgnoreCase("e")) {
                SimplePrinter.printParagraph();
                SimplePrinter.print(PrintUtil.LEFT_TAGS_MENU);
                return null;
            }

            if (tagNameAnswer.length() > TAG_MAX_LENGTH) {
                SimplePrinter.printParagraph();
                SimplePrinter.print(String.format("Tag name size must not be more than %d symbols!", TAG_MAX_LENGTH));
                SimplePrinter.printTryAgainMessage();
                SimplePrinter.printParagraph();
            } else if (StringUtils.isEmpty(tagNameAnswer)) {
                SimplePrinter.printParagraph();
                SimplePrinter.print("Tag must not be empty!");
                SimplePrinter.printTryAgainMessage();
                SimplePrinter.printParagraph();
            } else if (tagService.exists(tagNameAnswer)) {
                SimplePrinter.printParagraph();
                SimplePrinter.print(String.format("Tag with name [%s] already exists!", tagNameAnswer));
                SimplePrinter.printTryAgainMessage();
                SimplePrinter.printParagraph();
            } else {
                SimplePrinter.printParagraph();
                SimplePrinter.print(String.format("Are you sure you want to save tag with name [%s]?", tagNameAnswer));
                SimplePrinter.printYesNoQuestion();

                SimplePrinter.printParagraph();

                String answer = provideLine();

                SimplePrinter.printParagraph();

                if (answer.startsWith("y")) {
                    return tagNameAnswer;
                }

                SimplePrinter.printTryAgainMessage();
                SimplePrinter.printParagraph();
            }
        } while (true);
    }

    @Override
    public String action() {
        return "1";
    }

}

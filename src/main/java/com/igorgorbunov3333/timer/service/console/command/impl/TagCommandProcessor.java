package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagCommandSessionStarter;
import com.igorgorbunov3333.timer.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.tag.TagProvider;
import com.igorgorbunov3333.timer.service.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class TagCommandProcessor implements CommandProcessor {

    private final TagCommandSessionStarter tagCommandSessionStarter;
    @Getter
    private final TagService tagService;
    private final TagProvider tagProvider;

    @Override
    public void process() {
        boolean sessionFinished = false;

        Map<Integer, PomodoroTagDto> tagsWithNumbers = tagProvider.provide();
        ListOfItemsPrinter.print(tagsWithNumbers, PomodoroTagDto::getName);

        while (!sessionFinished) {
            sessionFinished = tagCommandSessionStarter.startSession(tagsWithNumbers);
        }
    }

    @Override
    public String command() {
        return "tag";
    }

}

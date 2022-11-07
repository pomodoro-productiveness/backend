package com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.service.console.command.impl.AbstractPomodoroSessionMapper;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.TagPomodoroSessionUpdater;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.DailySinglePomodoroFromUserProvider;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TagRemappingSessionProcessor extends AbstractPomodoroSessionMapper implements TagSessionProcessor {

    @Getter
    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final TagPomodoroSessionUpdater tagPomodoroSessionUpdater;

    private final DailySinglePomodoroFromUserProvider dailySinglePomodoroFromUserProvider;

    @Override
    public void process(Map<Integer, PomodoroTagDto> tagPositionToTags) {
        SimplePrinter.printParagraph();
        SimplePrinter.print("Chose pomodoro to remap tags:");
        PomodoroDto chosenPomodoro = dailySinglePomodoroFromUserProvider.provide();

        if (chosenPomodoro != null) {
            startTagSessionAndPrintDailyPomodoro(List.of(chosenPomodoro.getId()));
        }
    }

    @Override
    public String action() {
        return "3";
    }

}
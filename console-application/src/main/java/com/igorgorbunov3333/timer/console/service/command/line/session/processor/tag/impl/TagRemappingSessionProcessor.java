package com.igorgorbunov3333.timer.console.service.command.line.session.processor.tag.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.service.command.impl.AbstractPomodoroSessionMapper;
import com.igorgorbunov3333.timer.console.service.command.line.session.TagPomodoroSessionUpdater;
import com.igorgorbunov3333.timer.console.service.command.line.session.processor.tag.TagSessionProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.provider.DailySinglePomodoroFromUserProvider;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TagRemappingSessionProcessor extends AbstractPomodoroSessionMapper implements TagSessionProcessor {

    @Getter
    private final PomodoroComponent pomodoroComponent;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final TagPomodoroSessionUpdater tagPomodoroSessionUpdater;
    @Getter
    private final CurrentTimeComponent currentTimeComponent;

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

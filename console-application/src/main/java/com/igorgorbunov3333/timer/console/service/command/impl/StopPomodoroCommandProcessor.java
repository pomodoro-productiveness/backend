package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.command.line.provider.BaseLineProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.TagPomodoroSessionUpdater;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import com.igorgorbunov3333.timer.console.service.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StopPomodoroCommandProcessor extends AbstractPomodoroSessionMapper implements CommandProcessor, BaseLineProvider {

    private final PomodoroEngineService pomodoroEngineService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CurrentTimeComponent currentTimeComponent;
    @Getter
    private final PomodoroComponent pomodoroComponent;
    private final TagPomodoroSessionUpdater tagPomodoroSessionUpdater;
    private final StandardReportPrinter standardReportPrinter;
    private final PomodoroEngine pomodoroEngine;

    @Override
    public void process() {
        if (pomodoroEngine.isPomodoroPaused()) {
            SimplePrinter.print("Pomodoro paused. Are you sure you want to stop pomodoro?");
            SimplePrinter.printYesNoQuestion();
            SimplePrinter.printParagraph();

            String answer = provideLine();

            if (answer.startsWith("y")) {
                SimplePrinter.printParagraph();
            } else {
                SimplePrinter.printParagraph();
                SimplePrinter.print("Pomodoro stayed on pause!");
                return;
            }
        }

        PomodoroDto savedPomodoro;

        try {
            savedPomodoro = pomodoroEngineService.stopPomodoro();
        } catch (PomodoroEngineException e) {
            String errorMessage = e.getMessage();
            SimplePrinter.print(errorMessage);
            return;
        }

        printSuccessfullySavedMessage(savedPomodoro);

        List<PomodoroDto> dailyPomodoro = startTagSessionAndPrintDailyPomodoro(List.of(savedPomodoro.getId()));
        LocalDate currentDay = currentTimeComponent.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period, dailyPomodoro);
    }

    @Override
    public String command() {
        return "2";
    }

}

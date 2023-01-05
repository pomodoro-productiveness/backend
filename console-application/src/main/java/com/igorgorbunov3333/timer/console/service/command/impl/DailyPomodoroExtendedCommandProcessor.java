package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.console.service.pomodoro.engine.PomodoroEngineComponent;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import com.igorgorbunov3333.timer.console.service.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodoroExtendedCommandProcessor implements CommandProcessor {

    private final PomodoroEngineComponent pomodoroEngineComponent;
    private final PomodoroEngine pomodoroEngine;
    private final PomodoroComponent pomodoroComponent;
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final CurrentTimeComponent currentTimeComponent;

    @Override
    public void process() {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();

        List<PomodoroDto> pomodoro = pomodoroComponent.getPomodoro(today, today, null);

        printerService.printPomodoroWithIdsAndTags(pomodoro);

        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            printPomodoroCurrentDuration("Duration of the running pomodoro: ");
        }

        if (pomodoroEngine.isPomodoroPaused()) {
            printPomodoroCurrentDuration("Duration of the paused pomodoro: ");
        }

        LocalDate currentDay = currentTimeComponent.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period);
    }

    private void printPomodoroCurrentDuration(String message) {
        String pomodoroCurrentDuration = pomodoroEngineComponent.getPomodoroCurrentDuration();

        SimplePrinter.printParagraph();
        SimplePrinter.print(message + pomodoroCurrentDuration);
    }

    @Override
    public String command() {
        return "5";
    }

}

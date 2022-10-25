package com.igorgorbunov3333.timer.backend.service.console.command.impl;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.backend.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.backend.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodoroExtendedCommandProcessor implements CommandProcessor {

    private final PomodoroEngineService pomodoroEngineService;
    private final PomodoroEngine pomodoroEngine;
    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final CurrentTimeService currentTimeService;

    @Override
    @Transactional(readOnly = true)
    public void process() {
        List<PomodoroDto> pomodoro = currentDayLocalPomodoroProvider.provideForCurrentDay(null);

        printerService.printPomodoroWithIdsAndTags(pomodoro);

        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            printPomodoroCurrentDuration("Duration of the running pomodoro: ");
        }

        if (pomodoroEngine.isPomodoroPaused()) {
            printPomodoroCurrentDuration("Duration of the paused pomodoro: ");
        }

        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period, pomodoro);
    }

    private void printPomodoroCurrentDuration(String message) {
        String pomodoroCurrentDuration = pomodoroEngineService.getPomodoroCurrentDuration();

        SimplePrinter.printParagraph();
        SimplePrinter.print(message + pomodoroCurrentDuration);
    }

    @Override
    public String command() {
        return "5";
    }

}

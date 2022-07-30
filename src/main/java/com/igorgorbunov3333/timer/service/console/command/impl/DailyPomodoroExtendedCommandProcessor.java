package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;
    private final PrinterService printerService;
    private final StandardReportPrinter standardReportPrinter;
    private final CurrentTimeService currentTimeService;

    @Override
    @Transactional(readOnly = true)
    public void process() {
        List<PomodoroDto> pomodoro = currentDayLocalPomodoroProvider.provide(null);

        printerService.printPomodoroWithIdsAndTags(pomodoro);

        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            String pomodoroCurrentDuration = pomodoroEngineService.getPomodoroCurrentDuration();

            SimplePrinter.printParagraph();

            SimplePrinter.print("Currently running pomodoro duration: " + pomodoroCurrentDuration);
        }

        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period, pomodoro);
    }

    @Override
    public String command() {
        return "5";
    }

}

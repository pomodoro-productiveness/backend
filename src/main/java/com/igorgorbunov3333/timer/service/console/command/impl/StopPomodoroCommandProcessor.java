package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.provider.BaseLineProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
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
public class StopPomodoroCommandProcessor extends AbstractPomodoroSessionMapper implements CommandProcessor, BaseLineProvider {

    private final PomodoroEngineService pomodoroEngineService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;
    @Getter
    private final TagPomodoroSessionMapper tagPomodoroSessionMapper;
    private final StandardReportPrinter standardReportPrinter;
    private final CurrentTimeService currentTimeService;
    private final PomodoroEngine pomodoroEngine;

    @Override
    @Transactional
    public void process() {
        if (pomodoroEngine.isPomodoroPaused()) {
            SimplePrinter.print("Pomodoro paused. Are you sure you want to stop pomodoro?");
            SimplePrinter.printYesNoQuestion();

            String answer = provideLine();

            if (!answer.startsWith("y")) {
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
        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period, dailyPomodoro);
    }

    @Override
    public String command() {
        return "2";
    }

}

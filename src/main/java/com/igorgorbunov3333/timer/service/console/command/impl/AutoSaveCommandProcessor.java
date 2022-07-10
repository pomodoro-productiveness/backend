package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.StandardReportPrinter;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.saver.PomodoroAutoSaver;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AutoSaveCommandProcessor extends AbstractPomodoroSessionMapper implements CommandProcessor {

    private final PomodoroAutoSaver pomodoroAutoSaver;
    @Getter
    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final TagPomodoroSessionMapper tagPomodoroSessionMapper;
    private final StandardReportPrinter standardReportPrinter;
    private final PomodoroEngine pomodoroEngine;
    private final CurrentTimeService currentTimeService;

    @Override
    public void process() {
        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            printerService.print("The pomodoro cannot be saved automatically because other one works now");
            return;
        }

        if (pomodoroEngine.isPomodoroPaused()) {
            printerService.print("The pomodoro cannot be saved automatically because other one was paused already");
            return;
        }

        PomodoroDto savedPomodoro;
        try {
            savedPomodoro = pomodoroAutoSaver.save();
        } catch (PomodoroException e) {
            String errorMessage = e.getMessage();
            printerService.print(errorMessage);
            return;
        }
        printSuccessfullySavedMessage(savedPomodoro);

        List<PomodoroDto> dailyPomodoro = startTagSessionAndPrintDailyPomodoros(savedPomodoro.getId());

        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        PeriodDto period = new PeriodDto(currentDay.atStartOfDay(), currentDay.atTime(LocalTime.MAX));

        standardReportPrinter.print(period, dailyPomodoro);
    }

    @Override
    public String command() {
        return "save";
    }

}

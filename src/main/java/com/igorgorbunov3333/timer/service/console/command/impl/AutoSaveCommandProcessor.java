package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.command.work.time.calculation.CompletedStandardPrintable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.provider.local.impl.CurrentDayLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.saver.PomodoroAutoSaver;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AutoSaveCommandProcessor extends AbstractPomodoroSessionMapper implements CommandProcessor, CompletedStandardPrintable {

    private final PomodoroAutoSaver pomodoroAutoSaver;
    @Getter
    private final CurrentDayLocalPomodoroProvider currentDayLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final TagPomodoroSessionMapper tagPomodoroSessionMapper;
    @Getter
    private final EducationTimeStandardCalculatorCoordinator educationTimeStandardCalculatorCoordinator;
    @Getter
    private final WorkTimeStandardCalculatorCoordinator workTimeStandardCalculatorCoordinator;
    private final PomodoroEngine pomodoroEngine;

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

        startTagSessionAndPrintDailyPomodoros(savedPomodoro.getId());

        printCompletedStandard(PomodoroPeriod.DAY);
    }

    @Override
    public String command() {
        return "save";
    }

}

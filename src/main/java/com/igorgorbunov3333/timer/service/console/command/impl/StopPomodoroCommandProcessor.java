package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.command.work.time.calculation.CompletedStandardPrintable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroEngineException;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.local.impl.CurrentDayLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StopPomodoroCommandProcessor extends AbstractPomodoroSessionMapper implements CommandProcessor, CompletedStandardPrintable {

    private final PomodoroEngineService pomodoroEngineService;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final CurrentDayLocalPomodoroProvider currentDayLocalPomodoroProvider;
    @Getter
    private final TagPomodoroSessionMapper tagPomodoroSessionMapper;
    @Getter
    private final EducationTimeStandardCalculatorCoordinator educationTimeStandardCalculatorCoordinator;
    @Getter
    private final WorkTimeStandardCalculatorCoordinator workTimeStandardCalculatorCoordinator;

    @Override
    public void process() {
        PomodoroDto savedPomodoro;
        try {
            savedPomodoro = pomodoroEngineService.stopPomodoro();
        } catch (PomodoroEngineException e) {
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
        return "2";
    }

}

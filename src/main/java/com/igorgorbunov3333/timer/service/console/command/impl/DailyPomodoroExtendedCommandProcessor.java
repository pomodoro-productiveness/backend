package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.work.time.calculation.CompletedStandardPrintable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngine;
import com.igorgorbunov3333.timer.service.pomodoro.engine.PomodoroEngineService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodoroExtendedCommandProcessor implements CommandProcessor, CompletedStandardPrintable {

    private final PomodoroEngineService pomodoroEngineService;
    private final PomodoroEngine pomodoroEngine;
    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final WorkTimeStandardCalculatorCoordinator workTimeStandardCalculatorCoordinator;
    @Getter
    private final EducationTimeStandardCalculatorCoordinator educationTimeStandardCalculatorCoordinator;

    @Override
    @Transactional(readOnly = true)
    public void process() {
        List<PomodoroDto> pomodoros = currentDayLocalPomodoroProvider.provide(null);

        printerService.printPomodorosWithIdsAndTags(pomodoros);

        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            String pomodoroCurrentDuration = pomodoroEngineService.getPomodoroCurrentDuration();

            printerService.printParagraph();

            printerService.print("Currently running pomodoro duration: " + pomodoroCurrentDuration);
        }

        printCompletedStandard(PomodoroPeriod.DAY);

    }

    @Override
    public String command() {
        return "5";
    }

}

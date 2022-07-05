package com.igorgorbunov3333.timer.service.console.command.work.time.calculation;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;

import java.util.List;

//TODO: consider make it separate bean an autowire it as usual as needed and rename
public interface CompletedStandardCalculable {

    PrinterService getPrinterService();
    WorkTimeStandardCalculatorCoordinator getWorkTimeStandardCalculatorCoordinator();
    EducationTimeStandardCalculatorCoordinator getEducationTimeStandardCalculatorCoordinator();

    default void calculateStandard(PomodoroPeriod period, List<PomodoroDto> pomodoro) {
        getPrinterService().printParagraph();
        int workBalance = getWorkTimeStandardCalculatorCoordinator()
                .calculate(period, pomodoro);
        getPrinterService().print(String.format("Work performance: %s", workBalance));

        getPrinterService().printParagraph();
        int educationBalance = getEducationTimeStandardCalculatorCoordinator()
                .calculate(period, pomodoro);
        getPrinterService().print(String.format("Education performance: %s", educationBalance));
    }

}

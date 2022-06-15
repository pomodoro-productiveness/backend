package com.igorgorbunov3333.timer.service.console.command.work.time.calculation;

import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;

public abstract class CompletedStandardPrinter {

    public abstract PrinterService getPrinterService();
    public abstract WorkTimeStandardCalculatorCoordinator getWorkTimeStandardCalculatorCoordinator();
    public abstract EducationTimeStandardCalculatorCoordinator getEducationTimeStandardCalculatorCoordinator();

    public void printCompletedStandard(PomodoroPeriod period) {
        getPrinterService().printParagraph();
        int workBalance = getWorkTimeStandardCalculatorCoordinator()
                .calculate(period);
        getPrinterService().print(String.format("Work performance: %s", workBalance));

        getPrinterService().printParagraph();
        int educationBalance = getEducationTimeStandardCalculatorCoordinator()
                .calculate(period);
        getPrinterService().print(String.format("Education performance: %s", educationBalance));
    }

}

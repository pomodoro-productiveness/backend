package com.igorgorbunov3333.timer.service.console.command.work.time.calculation;

import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.WorkTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.enums.CalculationPeriod;

public abstract class WorkTimeCalculationPrinter {

    public abstract PrinterService getPrinterService();
    public abstract WorkTimeStandardCalculatorCoordinator getWorkTimeStandardCalculatorCoordinator();

    public void printWorkTimeCalculation(CalculationPeriod period) {
        getPrinterService().printParagraph();
        int balance = getWorkTimeStandardCalculatorCoordinator()
                .calculate(period)
                .getBalance();
        getPrinterService().print(String.format("Work performance: %s", balance));
    }

}

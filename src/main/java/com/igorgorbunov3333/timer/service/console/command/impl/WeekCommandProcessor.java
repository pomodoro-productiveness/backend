package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.work.time.calculation.CompletedStandardPrinter;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeekLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.CalculationPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WeekCommandProcessor extends CompletedStandardPrinter implements CommandProcessor {

    private final WeekLocalPomodoroProvider weekLocalPomodoroProvider;
    @Getter
    private final EducationTimeStandardCalculatorCoordinator educationTimeStandardCalculatorCoordinator;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final WorkTimeStandardCalculatorCoordinator workTimeStandardCalculatorCoordinator;

    @Override
    public void process() {
        Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros = weekLocalPomodoroProvider.provideCurrentWeekPomodorosByDays();
        if (weeklyPomodoros.isEmpty()) {
            printerService.print("No weekly pomodoros");
        }
        printerService.printDayOfWeekToPomodoros(weeklyPomodoros);

        printCompletedStandard(CalculationPeriod.WEEK);
    }

    @Override
    public String command() {
        return "week";
    }

}

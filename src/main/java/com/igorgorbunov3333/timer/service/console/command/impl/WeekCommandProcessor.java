package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.work.time.calculation.WorkTimeCalculationPrinter;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.WorkTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.enums.CalculationPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WeekCommandProcessor extends WorkTimeCalculationPrinter implements CommandProcessor {

    private final WeeklyLocalPomodoroProvider weeklyLocalPomodoroProvider;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final WorkTimeStandardCalculatorCoordinator workTimeStandardCalculatorCoordinator;

    @Override
    public void process() {
        Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros = weeklyLocalPomodoroProvider.provideCurrentWeekPomodorosByDays();
        if (weeklyPomodoros.isEmpty()) {
            printerService.print("No weekly pomodoros");
        }
        printerService.printDayOfWeekToPomodoros(weeklyPomodoros);

        printWorkTimeCalculation(CalculationPeriod.WEEK);
    }

    @Override
    public String command() {
        return "week";
    }

}

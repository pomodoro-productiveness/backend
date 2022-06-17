package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.work.time.calculation.CompletedStandardPrintable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentMonthLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculatorCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonthCommandProcessor implements CommandProcessor, CompletedStandardPrintable {

    private final CurrentMonthLocalPomodoroProvider currentMonthLocalPomodoroProvider;
    @Getter
    private final EducationTimeStandardCalculatorCoordinator educationTimeStandardCalculatorCoordinator;
    @Getter
    private final PrinterService printerService;
    @Getter
    private final WorkTimeStandardCalculatorCoordinator workTimeStandardCalculatorCoordinator;

    @Override
    public void process() {
        List<PomodoroDto> monthlyPomodoros = currentMonthLocalPomodoroProvider.provide(null);
        Map<LocalDate, List<PomodoroDto>> datesToPomadoros = monthlyPomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().toLocalDate()));
        Map<LocalDate, List<PomodoroDto>> sortedPomodoros = new TreeMap<>(datesToPomadoros);
        if (sortedPomodoros.isEmpty()) {
            printerService.print("No monthly pomodoros");
        }
        printerService.printLocalDatePomodoros(sortedPomodoros);

        printCompletedStandard(PomodoroPeriod.MONTH);
    }

    @Override
    public String command() {
        return "6";
    }

}

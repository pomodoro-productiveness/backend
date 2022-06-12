package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.WeeklyWorkingTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyLocalPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WeekCommandProcessor implements CommandProcessor {

    private final WeeklyLocalPomodoroProvider weeklyLocalPomodoroProvider;
    private final PrinterService printerService;
    private final WeeklyWorkingTimeStandardCalculator weeklyWorkingTimeStandardCalculator;

    @Override
    public void process() {
        Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros = weeklyLocalPomodoroProvider.provideCurrentWeekPomodorosByDays();
        if (weeklyPomodoros.isEmpty()) {
            printerService.print("No weekly pomodoros");
        }
        printerService.printDayOfWeekToPomodoros(weeklyPomodoros);

        printerService.printParagraph();
        int balance = weeklyWorkingTimeStandardCalculator.calculate().getBalance();
        printerService.print(String.format("Work performance: %s", balance));
    }

    @Override
    public String command() {
        return "week";
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroPeriodService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class WeekCommandProcessor implements CommandProcessor {

    private final PomodoroPeriodService pomodoroPeriodService;
    private final PrinterService printerService;

    @Override
    public void process() {
        Map<DayOfWeek, List<PomodoroDto>> weeklyPomodoros = pomodoroPeriodService.getCurrentWeekPomodoros();
        if (weeklyPomodoros.isEmpty()) {
            printerService.print("No weekly pomodoros");
        }
        printerService.printDayOfWeekToPomodoros(weeklyPomodoros);
    }

    @Override
    public String command() {
        return "week";
    }

}

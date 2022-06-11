package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.impl.PomodoroFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class MonthlyPomodorosCommandProcessor implements CommandProcessor {

    private final PomodoroFacade pomodoroFacade;
    private final PrinterService printerService;

    @Override
    public void process() {
        Map<LocalDate, List<PomodoroDto>> datesToPomadoros = pomodoroFacade.getMonthlyPomodoros();
        if (datesToPomadoros.isEmpty()) {
            printerService.print("No monthly pomodoros");
        }
        printerService.printLocalDatePomodoros(datesToPomadoros);
    }

    @Override
    public String command() {
        return "6";
    }

}

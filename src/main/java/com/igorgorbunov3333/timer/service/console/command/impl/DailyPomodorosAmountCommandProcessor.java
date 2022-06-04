package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DailyPomodorosAmountCommandProcessor implements CommandProcessor {

    private final PomodoroService pomodoroService;
    private final PrinterService printerService;

    @Override
    public void process() {
        long dailyPomodorosAmount = pomodoroService.getPomodorosInDay();
        printerService.print(String.valueOf(dailyPomodorosAmount));
    }

    @Override
    public String command() {
        return "4";
    }

}

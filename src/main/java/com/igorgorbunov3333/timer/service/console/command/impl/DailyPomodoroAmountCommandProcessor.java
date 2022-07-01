package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DailyPomodoroAmountCommandProcessor implements CommandProcessor {

    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;
    private final PrinterService printerService;

    @Override
    public void process() {
        long dailyPomodorosAmount = currentDayLocalPomodoroProvider.provide(null).size();
        printerService.print(String.valueOf(dailyPomodorosAmount));
    }

    @Override
    public String command() {
        return "4";
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.DailyLocalPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DailyPomodorosAmountCommandProcessor implements CommandProcessor {

    private final DailyLocalPomodoroProvider dailyLocalPomodoroProvider;
    private final PrinterService printerService;

    @Override
    public void process() {
        long dailyPomodorosAmount = dailyLocalPomodoroProvider.provideDailyLocalPomodoros().size();
        printerService.print(String.valueOf(dailyPomodorosAmount));
    }

    @Override
    public String command() {
        return "4";
    }

}

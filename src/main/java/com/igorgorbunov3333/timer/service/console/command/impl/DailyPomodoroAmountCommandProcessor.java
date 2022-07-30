package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DailyPomodoroAmountCommandProcessor implements CommandProcessor {

    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;

    @Override
    public void process() {
        long dailyPomodoroAmount = currentDayLocalPomodoroProvider.provide(null).size();
        SimplePrinter.print(String.valueOf(dailyPomodoroAmount));
    }

    @Override
    public String command() {
        return "4";
    }

}

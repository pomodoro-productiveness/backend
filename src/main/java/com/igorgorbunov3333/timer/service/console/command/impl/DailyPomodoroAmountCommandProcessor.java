package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.DailyPomodoroProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DailyPomodoroAmountCommandProcessor implements CommandProcessor {

    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;

    @Override
    public void process() {
        long dailyPomodoroAmount = currentDayLocalPomodoroProvider.provideForCurrentDay(null).size();
        SimplePrinter.print(String.valueOf(dailyPomodoroAmount));
    }

    @Override
    public String command() {
        return "4";
    }

}

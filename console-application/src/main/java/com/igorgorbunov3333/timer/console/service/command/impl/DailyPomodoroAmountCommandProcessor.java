package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class DailyPomodoroAmountCommandProcessor implements CommandProcessor {

    private final PomodoroComponent pomodoroComponent;
    private final CurrentTimeComponent currentTimeComponent;

    @Override
    public void process() {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();
        long dailyPomodoroAmount = pomodoroComponent.getPomodoro(today, today, null).size();
        SimplePrinter.print(String.valueOf(dailyPomodoroAmount));
    }

    @Override
    public String command() {
        return "4";
    }

}

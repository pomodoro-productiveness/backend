package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.YearlyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.console.service.command.line.session.coordinator.MonthSessionCommandCoordinator;
import com.igorgorbunov3333.timer.console.service.pomodoro.provider.YearlyPomodoroProvider;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MonthCommandProcessor implements CommandProcessor, NumberProvidable {

    private final YearlyPomodoroProvider yearlyPomodoroProvider;
    @Getter
    private final CommandProvider commandProvider;
    private final MonthSessionCommandCoordinator monthSessionCommandCoordinator;

    @Override
    public void process() {
        YearlyPomodoroDto yearlyPomodoro = yearlyPomodoroProvider.provideCurrentYearPomodoro(null);

        while (true) {
            SimplePrinter.print("Choose an option how to display monthly pomodoro or \"e\" to exit");
            SimplePrinter.print("1. Current month");
            SimplePrinter.print("2. Previous month");

            SimplePrinter.printParagraph();

            int numberAnswer = provideNumber();

            if (numberAnswer == -1) {
                break;
            }

            if (numberAnswer > 2) {
                SimplePrinter.print("Wrong number. Try again");
            } else {
                monthSessionCommandCoordinator.coordinate(String.valueOf(numberAnswer), yearlyPomodoro.getPomodoro());
            }
        }

        SimplePrinter.printParagraph();

        SimplePrinter.print("Monthly pomodoro menu abandoned");
    }

    @Override
    public String command() {
        return "6";
    }

}

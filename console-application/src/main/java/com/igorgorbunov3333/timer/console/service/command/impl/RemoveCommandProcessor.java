package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.CommandProcessor;
import com.igorgorbunov3333.timer.console.service.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.console.service.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.provider.DailySinglePomodoroFromUserProvider;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RemoveCommandProcessor implements CommandProcessor, NumberProvidable {

    private final PomodoroComponent pomodoroComponent;
    private final DailySinglePomodoroFromUserProvider dailySinglePomodoroFromUserProvider;

    @Getter
    private final CommandProvider commandProvider;

    @Override
    public void process() {
        SimplePrinter.print("Choose pomodoro to remove:");

        PomodoroDto chosenPomodoro = dailySinglePomodoroFromUserProvider.provide();

        if (chosenPomodoro == null) {
            return;
        }

        SimplePrinter.printParagraph();
        SimplePrinter.print("Are you sure you want to delete pomodoro " + chosenPomodoro);
        SimplePrinter.printYesNoQuestion();
        SimplePrinter.printParagraph();

        String answer = commandProvider.provideLine();

        if (!answer.toLowerCase().startsWith("y")) {
            SimplePrinter.printParagraph();
            SimplePrinter.print("Pomodoro will not be deleted");
            return;
        }

        pomodoroComponent.deletePomodoro(chosenPomodoro.getId());

        SimplePrinter.printParagraph();
        SimplePrinter.print("Pomodoro [" + chosenPomodoro + "] removed successfully");
    }

    @Override
    public String command() {
        return "remove";
    }

}

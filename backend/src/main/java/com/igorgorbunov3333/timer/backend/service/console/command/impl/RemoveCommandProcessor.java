package com.igorgorbunov3333.timer.backend.service.console.command.impl;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.backend.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.DailySinglePomodoroFromUserProvider;
import com.igorgorbunov3333.timer.backend.service.pomodoro.remover.PomodoroRemover;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RemoveCommandProcessor implements CommandProcessor, NumberProvidable {

    private final PomodoroRemover pomodoroRemover;
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

        pomodoroRemover.remove(chosenPomodoro.getId());

        SimplePrinter.printParagraph();
        SimplePrinter.print("Pomodoro [" + chosenPomodoro + "] removed successfully");
    }

    @Override
    public String command() {
        return "remove";
    }

}

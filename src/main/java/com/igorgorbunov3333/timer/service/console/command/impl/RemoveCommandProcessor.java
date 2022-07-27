package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.provider.CommandProvider;
import com.igorgorbunov3333.timer.service.console.command.line.session.NumberProvidable;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.DailySinglePomodoroFromUserProvider;
import com.igorgorbunov3333.timer.service.pomodoro.remover.PomodoroRemover;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RemoveCommandProcessor implements CommandProcessor, NumberProvidable {

    private final PomodoroRemover pomodoroRemover;
    private final DailySinglePomodoroFromUserProvider dailySinglePomodoroFromUserProvider;

    @Getter
    private final PrinterService printerService;
    @Getter
    private final CommandProvider commandProvider;

    @Override
    public void process() {
        printerService.printParagraph();
        printerService.print("Choose pomodoro to remove:");

        PomodoroDto chosenPomodoro = dailySinglePomodoroFromUserProvider.provide();

        if (chosenPomodoro == null) {
            return;
        }

        printerService.print("Are you sure you want to delete pomodoro " + chosenPomodoro);
        printerService.printYesNoQuestion();

        String answer = commandProvider.provideLine();

        if (!answer.toLowerCase().startsWith("y")) {
            printerService.print("Pomodoro will not be deleted");
            return;
        }

        pomodoroRemover.remove(chosenPomodoro.getId());

        printerService.print("Pomodoro [" + chosenPomodoro + "] removed successfully");
    }

    @Override
    public String command() {
        return "remove";
    }

}

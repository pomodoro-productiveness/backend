package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.impl.PomodoroFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: refactor
@Service
@AllArgsConstructor
public class RemoveCommandProcessor implements CommandProcessor {

    private final PomodoroFacade pomodoroFacade;
    private final PrinterService printerService;

    @Override
    public void process() {
        String input = CurrentCommandStorage.currentCommand;
        char[] inputChars = input.toCharArray();
        if (inputChars.length == "remove".length()) {
            List<PomodoroDto> dailyPomodoros = pomodoroFacade.getPomodorosInDayExtended();
            if (dailyPomodoros.isEmpty()) {
                printerService.print("Unable to remove latest pomodoro as no daily pomodoros");
                return;
            }
            Long removedPomodoroId;
            try {
                removedPomodoroId = pomodoroFacade.removeLatest();
            } catch (PomodoroException e) {
                printerService.print(e.getMessage());
                return;
            }
            if (removedPomodoroId != null) {
                printerService.print("Pomodoro with id " + removedPomodoroId + " successfully removed");
            }
            return;
        }
        int index = "remove ".length();
        if (inputChars[index - 1] != ' ') {
            printerService.print("Incorrect input \"" + input + "\". \"remove\" and id should be separated with \" \"");
            return;
        }
        String pomodoroIdArgument = getArgumentString(input, inputChars, index);
        Long pomodoroId = Long.valueOf(pomodoroIdArgument);
        try {
            pomodoroFacade.remove(pomodoroId);
        } catch (PomodoroException e) {
            printerService.print(e.getMessage());
        }
        printerService.print("Pomodoro with id [" + pomodoroId + "] removed");
    }

    private String getArgumentString(String input, char[] inputChars, int index) {
        char[] pomodoroIdInString = new char[input.length() - index];
        for (int i = index, j = 0; i < inputChars.length; i++, j++) {
            pomodoroIdInString[j] = inputChars[i];
        }
        return new String(pomodoroIdInString);
    }

    @Override
    public String command() {
        return "remove";
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandService;
import com.igorgorbunov3333.timer.service.console.command.CurrentCommandStorage;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RemoveCommandService implements CommandService {

    private final PomodoroService pomodoroService;

    @Override
    public void process() {
        String input = CurrentCommandStorage.currentCommand;
        char[] inputChars = input.toCharArray();
        if (inputChars.length == "remove".length()) {
            List<PomodoroDto> dailyPomodoros = pomodoroService.getPomodorosInDayExtended();
            if (dailyPomodoros.isEmpty()) {
                System.out.println("Unable to remove latest pomodoro as no daily pomodoros");
                return;
            }
            Long removedPomodoroId;
            try {
                removedPomodoroId = pomodoroService.removeLatest();
            } catch (PomodoroException e) {
                System.out.println(e.getMessage());
                return;
            }
            if (removedPomodoroId != null) {
                System.out.println("Pomodoro with id " + removedPomodoroId + " successfully removed");
            }
            return;
        }
        int index = "remove ".length();
        if (inputChars[index - 1] != ' ') {
            System.out.println("Incorrect input \"" + input + "\". \"remove\" and id should be separated with \" \"");
            return;
        }
        String pomodoroIdArgument = getArgumentString(input, inputChars, index);
        Long pomodoroId = Long.valueOf(pomodoroIdArgument);
        try {
            pomodoroService.removePomodoro(pomodoroId);
        } catch (PomodoroException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Pomodoro with id [" + pomodoroId + "] removed");
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

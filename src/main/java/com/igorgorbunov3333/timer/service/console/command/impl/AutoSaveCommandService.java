package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandService;
import com.igorgorbunov3333.timer.service.console.command.line.provider.TagProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class AutoSaveCommandService extends AbstractPomodoroCommandService implements CommandService {

    private final PomodoroService pomodoroService;
    private final PrinterService printerService;
    private final TagProvider tagProvider;

    @Override
    public void process() {
        PomodoroDto savedPomodoro;
        try {
            savedPomodoro = pomodoroService.saveAutomatically();
        } catch (PomodoroException e) {
            String errorMessage = e.getMessage();
            printerService.print(errorMessage);
            return;
        }

        printSuccessfullySavedMessage(savedPomodoro);

        addTagToPomodoroAndPrintDailyPomodoros(savedPomodoro.getId());
    }

    @Override
    public String command() {
        return "save";
    }

}

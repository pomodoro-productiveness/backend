package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.exception.PomodoroException;
import com.igorgorbunov3333.timer.service.pomodoro.impl.PomodoroFacade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class AutoSaveCommandProcessor extends AbstractPomodoroCommandService implements CommandProcessor {

    private final PomodoroFacade pomodoroFacade;
    private final PrinterService printerService;
    private final TagPomodoroSessionMapper tagPomodoroSessionMapper;

    @Override
    public void process() {
        PomodoroDto savedPomodoro;
        try {
            savedPomodoro = pomodoroFacade.saveAutomatically();
        } catch (PomodoroException e) {
            String errorMessage = e.getMessage();
            printerService.print(errorMessage);
            return;
        }
        printSuccessfullySavedMessage(savedPomodoro);

        startTagSessionAndPrintDailyPomodoros(savedPomodoro.getId());
    }

    @Override
    public String command() {
        return "save";
    }

}

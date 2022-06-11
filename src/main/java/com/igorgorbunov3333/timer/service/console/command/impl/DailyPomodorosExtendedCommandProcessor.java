package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.impl.PomodoroFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodorosExtendedCommandProcessor implements CommandProcessor {

    private final PomodoroFacade pomodoroFacade;
    private final PrinterService printerService;

    @Override
    public void process() {
        List<PomodoroDto> pomodoros = pomodoroFacade.getPomodorosInDayExtended();
        printerService.printPomodorosWithIdsAndTags(pomodoros);
    }

    @Override
    public String command() {
        return "5";
    }

}

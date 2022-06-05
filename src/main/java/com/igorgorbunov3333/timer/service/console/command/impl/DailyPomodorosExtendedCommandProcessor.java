package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandProcessor;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodorosExtendedCommandProcessor implements CommandProcessor {

    private final PomodoroService pomodoroService;
    private final PrinterService printerService;

    @Override
    public void process() {
        List<PomodoroDto> pomodoros = pomodoroService.getPomodorosInDayExtended();
        printerService.printPomodorosWithIdsAndTags(pomodoros);
    }

    @Override
    public String command() {
        return "5";
    }

}

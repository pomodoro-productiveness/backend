package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.CommandService;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodorosExtendedCommandService implements CommandService {

    private final PomodoroService pomodoroService;
    private final PrinterService printerService;

    @Override
    public void process() {
        List<PomodoroDto> pomodoros = pomodoroService.getPomodorosInDayExtended();
        printerService.printPomodorosWithIds(pomodoros);
    }

    @Override
    public String command() {
        return "5";
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.PomodoroTag;
import com.igorgorbunov3333.timer.service.console.command.line.provider.TagProvider;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;

import java.util.List;
import java.util.Optional;

public abstract class AbstractPomodoroCommandService {

    public abstract PomodoroService getPomodoroService();
    public abstract PrinterService getPrinterService();
    public abstract TagProvider getTagProvider();

    public void printSuccessfullySavedMessage(PomodoroDto savedPomodoro) {
        String successfullySavedMessage = DefaultPrinterService.MESSAGE_POMODORO_SAVED + savedPomodoro;
        getPrinterService().print(successfullySavedMessage);
    }

    public void addTagToPomodoroAndPrintDailyPomodoros(Long pomodoroId) {
        Optional<PomodoroTag> pomodoroTagOptional = getTagProvider().provideTag();

        pomodoroTagOptional.ifPresent(pomodoroTag ->
                getPomodoroService().updatePomodoroWithTag(pomodoroId, pomodoroTag));

        getAndPrintDailyPomodoros();
    }

    private void getAndPrintDailyPomodoros() {
        List<PomodoroDto> pomodoros = getPomodoroService().getPomodorosInDayExtended();
        getPrinterService().printPomodorosWithIds(pomodoros);
    }

}

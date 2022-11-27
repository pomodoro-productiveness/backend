package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.TagPomodoroSessionUpdater;
import com.igorgorbunov3333.timer.backend.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;

import java.util.List;

public abstract class AbstractPomodoroSessionMapper {

    public abstract DailyPomodoroProvider getCurrentDayLocalPomodoroProvider();
    public abstract PrinterService getPrinterService();
    public abstract TagPomodoroSessionUpdater getTagPomodoroSessionUpdater();

    public void printSuccessfullySavedMessage(PomodoroDto savedPomodoro) {
        String successfullySavedMessage = PrintUtil.MESSAGE_POMODORO_SAVED + savedPomodoro;
        SimplePrinter.print(successfullySavedMessage);
    }

    //TODO: must be another session service to only map tag to pomodoro
    public List<PomodoroDto> startTagSessionAndPrintDailyPomodoro(List<Long> pomodoroId) {
        SimplePrinter.printParagraph();

        getTagPomodoroSessionUpdater().addTagToPomodoro(pomodoroId);

        return getAndPrintCurrentDayPomodoro();
    }

    private List<PomodoroDto> getAndPrintCurrentDayPomodoro() {
        List<PomodoroDto> pomodoro = getCurrentDayLocalPomodoroProvider().provideForCurrentDay(null);
        getPrinterService().printPomodoroWithIdsAndTags(pomodoro);

        return pomodoro;
    }

}

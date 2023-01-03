package com.igorgorbunov3333.timer.console.service.command.impl;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.service.command.line.session.TagPomodoroSessionUpdater;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.printer.PrinterService;
import com.igorgorbunov3333.timer.console.service.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;

import java.time.LocalDate;
import java.util.List;

public abstract class AbstractPomodoroSessionMapper {

    public abstract PrinterService getPrinterService();
    public abstract TagPomodoroSessionUpdater getTagPomodoroSessionUpdater();
    public abstract PomodoroComponent getPomodoroComponent();
    public abstract CurrentTimeComponent getCurrentTimeComponent();

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
        LocalDate today = getCurrentTimeComponent().getCurrentDateTime().toLocalDate();
        List<PomodoroDto> pomodoro = getPomodoroComponent().getPomodoro(today, today, null);
        getPrinterService().printPomodoroWithIdsAndTags(pomodoro);

        return pomodoro;
    }

}

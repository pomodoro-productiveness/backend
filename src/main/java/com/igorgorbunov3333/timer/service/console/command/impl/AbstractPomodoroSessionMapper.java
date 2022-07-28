package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;

import java.util.List;

public abstract class AbstractPomodoroSessionMapper {

    public abstract CurrentDayPomodoroProvider getCurrentDayLocalPomodoroProvider();
    public abstract PrinterService getPrinterService();
    public abstract TagPomodoroSessionMapper getTagPomodoroSessionMapper();

    public void printSuccessfullySavedMessage(PomodoroDto savedPomodoro) {
        String successfullySavedMessage = DefaultPrinterService.MESSAGE_POMODORO_SAVED + savedPomodoro;
        getPrinterService().print(successfullySavedMessage);
    }

    //TODO: must be another session service to only map tag to pomodoro
    public List<PomodoroDto> startTagSessionAndPrintDailyPomodoro(List<Long> pomodoroId) {
        getPrinterService().printParagraph();

        getTagPomodoroSessionMapper().addTagToPomodoro(pomodoroId);

        return getAndPrintDailyPomodoros();
    }

    private List<PomodoroDto> getAndPrintDailyPomodoros() {
        List<PomodoroDto> pomodoro = getCurrentDayLocalPomodoroProvider().provide(null);
        getPrinterService().printPomodorosWithIdsAndTags(pomodoro);

        return pomodoro;
    }

}

package com.igorgorbunov3333.timer.service.console.command.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.console.command.line.session.TagPomodoroSessionMapper;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.local.impl.CurrentDayLocalPomodoroProvider;

import java.util.List;

public abstract class AbstractPomodoroSessionMapper {

    public abstract CurrentDayLocalPomodoroProvider getCurrentDayLocalPomodoroProvider();
    public abstract PrinterService getPrinterService();
    public abstract TagPomodoroSessionMapper getTagPomodoroSessionMapper();

    public void printSuccessfullySavedMessage(PomodoroDto savedPomodoro) {
        String successfullySavedMessage = DefaultPrinterService.MESSAGE_POMODORO_SAVED + savedPomodoro;
        getPrinterService().print(successfullySavedMessage);
    }

    //TODO: must be another session service to only map tag to pomodoro
    public void startTagSessionAndPrintDailyPomodoros(Long pomodoroId) {
        getTagPomodoroSessionMapper().addTagToPomodoro(pomodoroId);

        getAndPrintDailyPomodoros();
    }

    private void getAndPrintDailyPomodoros() {
        List<PomodoroDto> pomodoros = getCurrentDayLocalPomodoroProvider().provide(null);
        getPrinterService().printPomodorosWithIdsAndTags(pomodoros);
    }

}

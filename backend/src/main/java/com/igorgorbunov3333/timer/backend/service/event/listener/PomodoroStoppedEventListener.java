package com.igorgorbunov3333.timer.backend.service.event.listener;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.event.PomodoroStoppedSpringEvent;
import com.igorgorbunov3333.timer.backend.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.backend.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.pomodoro.saver.PomodoroSaver;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PomodoroStoppedEventListener implements ApplicationListener<PomodoroStoppedSpringEvent> {

    private final DailyPomodoroProvider currentDayLocalPomodoroProvider;
    private final PomodoroSaver pomodoroSaver;
    private final PrinterService printerService;

    @Override
    public void onApplicationEvent(PomodoroStoppedSpringEvent event) {
        //TODO: implement logic
//        int pomodoroDuration = event.getPomodoroDuration();
//        PomodoroDto savedPomodoro = pomodoroSaver.save(pomodoroDuration);
//        String successfullySavedMessage = PrintUtil.MESSAGE_POMODORO_SAVED + savedPomodoro;
//
//        SimplePrinter.printParagraph();
//        SimplePrinter.print(successfullySavedMessage);
//        SimplePrinter.printParagraph();
//
//        List<PomodoroDto> pomodoro = currentDayLocalPomodoroProvider.provideForCurrentDay(null);
//        printerService.printPomodoroWithIdsAndTags(pomodoro);
    }

}

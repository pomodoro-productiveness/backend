package com.igorgorbunov3333.timer.service.event.listener;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.event.PomodoroStoppedSpringEvent;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.impl.PomodoroFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PomodoroStoppedEventListener implements ApplicationListener<PomodoroStoppedSpringEvent> {

    private final PomodoroFacade pomodoroFacade;
    private final PrinterService printerService;

    @Override
    public void onApplicationEvent(PomodoroStoppedSpringEvent event) {
        int pomodoroDuration = event.getPomodoroDuration();
        PomodoroDto savedPomodoro = pomodoroFacade.save(pomodoroDuration);
        List<PomodoroDto> dailyPomodoros = pomodoroFacade.getPomodorosInDayExtended();
        printerService.printSavedAndDailyPomodorosAfterStoppingPomodoro(savedPomodoro, dailyPomodoros);
    }

}

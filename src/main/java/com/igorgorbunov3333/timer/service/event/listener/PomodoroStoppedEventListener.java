package com.igorgorbunov3333.timer.service.event.listener;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.event.PomodoroStoppedSpringEvent;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.CurrentDayPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.saver.PomodoroSaver;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PomodoroStoppedEventListener implements ApplicationListener<PomodoroStoppedSpringEvent> {

    private final CurrentDayPomodoroProvider currentDayLocalPomodoroProvider;
    private final PomodoroSaver pomodoroSaver;
    private final PrinterService printerService;

    @Override
    public void onApplicationEvent(PomodoroStoppedSpringEvent event) {
        int pomodoroDuration = event.getPomodoroDuration();
        PomodoroDto savedPomodoro = pomodoroSaver.save(pomodoroDuration);
        List<PomodoroDto> dailyPomodoros = currentDayLocalPomodoroProvider.provide(null);
        printerService.printSavedAndDailyPomodoroAfterStoppingPomodoro(savedPomodoro, dailyPomodoros);
    }

}

package com.igorgorbunov3333.timer.service.event.listener;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.event.PomodoroStoppedSpringEvent;
import com.igorgorbunov3333.timer.service.commandline.PrinterService;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PomodoroStoppedEventListener implements ApplicationListener<PomodoroStoppedSpringEvent> {

    private final PomodoroService pomodoroService;
    private final PrinterService printerService;

    @Override
    public void onApplicationEvent(PomodoroStoppedSpringEvent event) {
        int pomodoroDuration = event.getPomodoroDuration();
        PomodoroDto savedPomodoro = pomodoroService.saveByDuration(pomodoroDuration);
        List<PomodoroDto> dailyPomodoros = pomodoroService.getPomodorosInDayExtended();
        printerService.printSavedAndDailyPomodorosAfterStoppingPomodoro(savedPomodoro, dailyPomodoros);
    }

}

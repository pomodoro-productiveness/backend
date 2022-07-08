package com.igorgorbunov3333.timer.service.pomodoro.engine;

import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import com.igorgorbunov3333.timer.service.console.printer.PrinterService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PomodoroPauseTimer {

    private final PomodoroEngine pomodoroEngine;
    private final PrinterService printerService;
    private final AudioPlayerService player;
    private final PomodoroEngineService pomodoroEngineService;

    @Async
    public void conduct(int seconds) {
        long start = System.currentTimeMillis();

        while (true) {
            long currentDurationInSeconds = (System.currentTimeMillis() - start) / 1000;

            if (currentDurationInSeconds >= seconds) {
                player.play();

                printerService.print(String.format("[%d] seconds have passed.", seconds));

                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                player.stop();

                pomodoroEngine.pausePomodoro();
                printerService.print(String.format("Pomodoro paused at %s", pomodoroEngineService.getPomodoroCurrentDuration()));

                break;
            }
        }

        pomodoroEngine.pausePomodoro();
    }

}

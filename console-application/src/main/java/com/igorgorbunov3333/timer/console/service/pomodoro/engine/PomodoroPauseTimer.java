package com.igorgorbunov3333.timer.console.service.pomodoro.engine;

import com.igorgorbunov3333.timer.console.service.audioplayer.AudioPlayerService;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PomodoroPauseTimer {

    private final PomodoroEngine pomodoroEngine;
    private final AudioPlayerService player;
    private final PomodoroEngineComponent pomodoroEngineComponent;

    @Async
    public void conduct(int seconds) {
        long start = System.currentTimeMillis();

        while (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            long currentDurationInMilliseconds = System.currentTimeMillis() - start;

            if (currentDurationInMilliseconds >= (seconds * 1000L)) {
                pomodoroEngine.pausePomodoro();

                SimplePrinter.print(String.format("[%s] minutes have passed", SecondsFormatter.formatInMinutes(seconds)));

                player.play();

                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                player.stop();

                SimplePrinter.print(String.format("Pomodoro paused at %s", pomodoroEngineComponent.getPomodoroCurrentDuration()));
                SimplePrinter.printParagraph();

                break;
            }
        }

        if (pomodoroEngine.isPomodoroCurrentlyRunning()) {
            pomodoroEngine.pausePomodoro();
        }
    }

}

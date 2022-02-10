package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
public class PomodoroEngine {

    private static final int SECONDS_IN_20_MINUTES = 1200;

    private final AudioPlayerService player;

    @Async
    public void startPomodoro() {
        startPomodoro(0);
    }

    public void stopPomodoro() {
        PomodoroState.running = false;
        PomodoroState.paused = false;
        player.stop();

        PomodoroState.duration.set(0);
    }

    public int getPomodoroCurrentDuration() {
        return PomodoroState.duration.get();
    }

    public boolean isPomodoroCurrentlyRunning() {
        return PomodoroState.running;
    }

    public boolean isPomodoroPaused() {
        return PomodoroState.paused;
    }

    public void pausePomodoro() {
        PomodoroState.paused = true;
        PomodoroState.running = false;
     }

    @Async
    public void resumePomodoro() {
        PomodoroState.paused = false;
        startPomodoro(PomodoroState.duration.get());
    }

    @SneakyThrows
    private void startPomodoro(int currentDuration) {
        if (PomodoroState.running) {
            return;
        }
        PomodoroState.running = true;
        PomodoroState.duration.set(currentDuration);
        boolean playerStarted = false;
        do {
            Thread.sleep(1000);
            int currentValue = PomodoroState.duration.incrementAndGet();
            if (currentValue >= SECONDS_IN_20_MINUTES && !playerStarted) {
                player.play();
                playerStarted = true;
            }
        } while (PomodoroState.running);
    }

    private static class PomodoroState {

        private static final AtomicInteger duration = new AtomicInteger(0);
        private static boolean running;
        private static boolean paused;
    }

}

package com.igorgorbunov3333.timer.service.pomodoro.engine;

import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import com.igorgorbunov3333.timer.service.event.publisher.PomodoroStoppedSpringEventPublisher;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
public class PomodoroEngine {

    private static final int SECONDS_IN_20_MINUTES = 20 * 60;
    private static final int SECONDS_IN_25_MINUTES = 25 * 60;

    private final AudioPlayerService player;
    private final PomodoroStoppedSpringEventPublisher pomodoroStoppedSpringEventPublisher;

    @Async
    public void startPomodoro() {
        startPomodoro(0);
    }

    public int stopPomodoro() {
        PomodoroState.running.set(false);
        PomodoroState.paused.set(false);
        final int pomodoroDuration = PomodoroState.duration.get();
        player.stop();
        PomodoroState.duration.set(0);
        return pomodoroDuration;
    }

    public int getPomodoroCurrentDuration() {
        return PomodoroState.duration.get();
    }

    public boolean isPomodoroCurrentlyRunning() {
        return PomodoroState.running.get();
    }

    public boolean isPomodoroPaused() {
        return PomodoroState.paused.get();
    }

    public void pausePomodoro() {
        PomodoroState.paused.set(true);
        PomodoroState.running.set(false);
     }

    @Async
    public void resumePomodoro() {
        PomodoroState.paused.set(false);
        startPomodoro(PomodoroState.duration.get());
    }

    @SneakyThrows
    private void startPomodoro(int currentDuration) {
        if (PomodoroState.running.get()) {
            return;
        }
        PomodoroState.running.set(true);
        PomodoroState.duration.set(currentDuration);
        boolean playerStarted = false;
        do {
            Thread.sleep(1000);
            int currentValue = PomodoroState.duration.incrementAndGet();
            if (currentValue >= SECONDS_IN_20_MINUTES && !playerStarted) {
                player.play();
                playerStarted = true;
            }
            if (currentValue >= SECONDS_IN_25_MINUTES) {
                final int pomodoroCurrentDuration = PomodoroState.duration.get();
                stopPomodoro();
                pomodoroStoppedSpringEventPublisher.publish(pomodoroCurrentDuration);
            }
        } while (PomodoroState.running.get());
    }

    private static class PomodoroState {

        private static final AtomicInteger duration = new AtomicInteger(0);
        private static final AtomicBoolean running = new AtomicBoolean(false);
        private static final AtomicBoolean paused = new AtomicBoolean(false);

    }

}

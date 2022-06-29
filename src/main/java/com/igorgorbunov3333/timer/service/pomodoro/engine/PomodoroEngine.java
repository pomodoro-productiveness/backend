package com.igorgorbunov3333.timer.service.pomodoro.engine;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import com.igorgorbunov3333.timer.service.event.publisher.PomodoroStoppedSpringEventPublisher;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
public class PomodoroEngine {

    private final AudioPlayerService player;
    private final PomodoroStoppedSpringEventPublisher pomodoroStoppedSpringEventPublisher;
    private final PomodoroPausesStorage pomodoroPausesStorage;
    private final PomodoroProperties pomodoroProperties;

    @Async
    public void startPomodoro() {
        startPomodoro(0);
    }

    public int stopPomodoro() {
        PomodoroState.POMODORO_RUNNING.set(false);
        PomodoroState.POMODORO_PAUSED.set(false);
        final int pomodoroDuration = PomodoroState.POMODORO_DURATION.get();
        player.stop();
        PomodoroState.POMODORO_DURATION.set(0);
        return pomodoroDuration;
    }

    public int getPomodoroCurrentDuration() {
        return PomodoroState.POMODORO_DURATION.get();
    }

    public boolean isPomodoroCurrentlyRunning() {
        return PomodoroState.POMODORO_RUNNING.get();
    }

    public boolean isPomodoroPaused() {
        return PomodoroState.POMODORO_PAUSED.get();
    }

    public void pausePomodoro() {
        PomodoroState.POMODORO_PAUSED.set(true);
        PomodoroState.POMODORO_RUNNING.set(false);
        PomodoroPauseState.pomodoroPauseStartTime = System.currentTimeMillis();
     }

    @Async
    public void resumePomodoro() {
        PomodoroState.POMODORO_PAUSED.set(false);
        PomodoroPauseState.pomodoroPauseEndTime = System.currentTimeMillis();
        pomodoroPausesStorage.add(Pair.of(PomodoroPauseState.pomodoroPauseStartTime, PomodoroPauseState.pomodoroPauseEndTime));
        startPomodoro(PomodoroState.POMODORO_DURATION.get());
    }

    @SneakyThrows
    private void startPomodoro(int currentDuration) {
        if (PomodoroState.POMODORO_RUNNING.get()) {
            return;
        }
        PomodoroState.POMODORO_RUNNING.set(true);
        PomodoroState.POMODORO_DURATION.set(currentDuration);
        boolean playerStarted = false;
        do {
            Thread.sleep(1000);
            int currentValue = PomodoroState.POMODORO_DURATION.incrementAndGet();
            if (currentValue >= pomodoroProperties.getDuration() && !playerStarted) {
                player.play();
                playerStarted = true;
            }
            if (currentValue >= pomodoroProperties.getAutomaticShutdownDuration()) {
                final int pomodoroCurrentDuration = PomodoroState.POMODORO_DURATION.get();
                stopPomodoro();
                pomodoroStoppedSpringEventPublisher.publish(pomodoroCurrentDuration);
            }
        } while (PomodoroState.POMODORO_RUNNING.get());
    }

    private static class PomodoroState {

        //TODO: convert atomics to plane types?
        private static final AtomicInteger POMODORO_DURATION = new AtomicInteger(0);
        private static final AtomicBoolean POMODORO_RUNNING = new AtomicBoolean(false);
        private static final AtomicBoolean POMODORO_PAUSED = new AtomicBoolean(false);

    }

    private static class PomodoroPauseState {

        private static long pomodoroPauseStartTime = 0;
        private static long pomodoroPauseEndTime = 0;

    }

}

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

@Component
@AllArgsConstructor
public class PomodoroEngine {

    private final AudioPlayerService player;
    private final PomodoroStoppedSpringEventPublisher pomodoroStoppedSpringEventPublisher;
    private final PomodoroPausesStorage pomodoroPausesStorage;
    private final PomodoroProperties pomodoroProperties;

    @Async
    public void startPomodoro() {
        startPomodoro(null);
    }

    public int stopPomodoro() {
        PomodoroState.POMODORO_RUNNING.set(false);
        PomodoroState.POMODORO_PAUSED.set(false);
        final int pomodoroDuration = (int) PomodoroState.currentPomodoroDurationInMilliseconds / 1000;
        player.stop();
        PomodoroState.currentPomodoroDurationInMilliseconds = 0;
        return pomodoroDuration;
    }

    public int getPomodoroCurrentDuration() {
        return (int) (PomodoroState.currentPomodoroDurationInMilliseconds / 1000);
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
        startPomodoro(PomodoroState.currentPomodoroDurationInMilliseconds);
    }

    @SneakyThrows
    private void startPomodoro(Long currentDuration) {
        long startMillis = System.currentTimeMillis();

        if (PomodoroState.POMODORO_RUNNING.get()) {
            return;
        }
        PomodoroState.POMODORO_RUNNING.set(true);

        long additionalDuration = currentDuration == null ? 0 : currentDuration;

        boolean playerStarted = false;
        do {
            PomodoroState.currentPomodoroDurationInMilliseconds = System.currentTimeMillis() - startMillis + additionalDuration;
            if (PomodoroState.currentPomodoroDurationInMilliseconds >= (pomodoroProperties.getDuration() * 60 * 1000) && !playerStarted) {
                player.play();
                playerStarted = true;
            }
            if (PomodoroState.currentPomodoroDurationInMilliseconds >= (pomodoroProperties.getAutomaticShutdownDuration() * 60 * 1000)) { //TODO: validate this value, it must be less then standard duration
                stopPomodoro();
                pomodoroStoppedSpringEventPublisher.publish((int) (PomodoroState.currentPomodoroDurationInMilliseconds / 1000));
            }
        } while (PomodoroState.POMODORO_RUNNING.get());
    }

    private static class PomodoroState {

        //TODO: convert atomics to plane types?
        private static volatile long currentPomodoroDurationInMilliseconds;
        private static final AtomicBoolean POMODORO_RUNNING = new AtomicBoolean(false);
        private static final AtomicBoolean POMODORO_PAUSED = new AtomicBoolean(false);

    }

    private static class PomodoroPauseState {

        private static long pomodoroPauseStartTime = 0;
        private static long pomodoroPauseEndTime = 0;

    }

}

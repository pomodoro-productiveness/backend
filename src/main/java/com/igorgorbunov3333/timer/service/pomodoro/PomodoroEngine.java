package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PomodoroEngine {

    private static final int SECONDS_IN_20_MINUTES = 1200;

    private final AudioPlayerService player;
    private final PomodoroProperties pomodoroProperties;

    @Async
    public void startPomodoro() {
        if (PomodoroState.running) {
            return;
        }
        PomodoroState.running = true;
        PomodoroState.duration = 0;
        boolean playerStarted = false;
        do {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
            PomodoroState.duration += 1;
            if (PomodoroState.duration >= SECONDS_IN_20_MINUTES && !playerStarted) {
                player.play();
                playerStarted = true;
            }
        } while (PomodoroState.running);
    }

    public void stopPomodoro() {
        PomodoroState.running = false;
        player.stop();

        PomodoroState.duration = 0;
    }

    public int getPomodoroCurrentDuration() {
        return PomodoroState.duration;
    }

    public long getPomodoroMinimumDuration() {
        Long pomodoroDuration = pomodoroProperties.getMinimumLifetime();
        return pomodoroDuration == null ? 0 : pomodoroDuration;
    }

    public boolean isPomodoroCurrentlyRunning() {
        return PomodoroState.running;
    }

    private static class PomodoroState {

        private static int duration;
        private static boolean running;

    }

}

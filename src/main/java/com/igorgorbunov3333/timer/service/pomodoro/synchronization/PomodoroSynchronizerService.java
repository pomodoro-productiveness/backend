package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

import java.time.LocalDateTime;

public interface PomodoroSynchronizerService {

    void synchronizeAfterRemovingPomodoro(LocalDateTime timestamp);

    void synchronize(LocalDateTime timestamp);

}

package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

public interface PomodoroSynchronizerService {

    void synchronizeAfterRemovingPomodoro(Long pomodoroId);

    void synchronize();

}

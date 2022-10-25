package com.igorgorbunov3333.timer.backend.service.pomodoro.engine;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;

public interface PomodoroEngineService {

    void startPomodoro();

    PomodoroDto stopPomodoro();

    String getPomodoroCurrentDuration();

    String getPomodoroCurrentDurationInString();

    void pausePomodoro();

    void resumePomodoro();

    void printThreeSecondsOfPomodoroExecution();

}

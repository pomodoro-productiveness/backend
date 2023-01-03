package com.igorgorbunov3333.timer.console.service.pomodoro.engine;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;

public interface PomodoroEngineService {

    void startPomodoro();

    PomodoroDto stopPomodoro();

    String getPomodoroCurrentDuration();

    String getPomodoroCurrentDurationInString();

    void pausePomodoro();

    void resumePomodoro();

    void printThreeSecondsOfPomodoroExecution();

}

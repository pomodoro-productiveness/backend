package com.igorgorbunov3333.timer.service.pomodoro.engine;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;

public interface PomodoroEngineService {

    void startPomodoro();

    PomodoroDto stopPomodoro();

    String getPomodoroCurrentDuration();

    String getPomodoroCurrentDurationInString();

    void pausePomodoro();

    void resumePomodoro();
}

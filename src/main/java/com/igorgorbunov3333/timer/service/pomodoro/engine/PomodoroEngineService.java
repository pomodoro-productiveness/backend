package com.igorgorbunov3333.timer.service.pomodoro.engine;

import com.igorgorbunov3333.timer.model.dto.engine.PomodoroActionInfoDto;

public interface PomodoroEngineService {

    PomodoroActionInfoDto startPomodoro();

    PomodoroActionInfoDto stopPomodoro();

    String getPomodoroCurrentDuration();

    String getPomodoroCurrentDurationInString();
}

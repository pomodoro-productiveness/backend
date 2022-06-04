package com.igorgorbunov3333.timer.service.pomodoro;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDataDto;

public interface RemotePomodoroDataService {

    PomodoroDataDto getRemoteData();

    void updateRemoteData(PomodoroDataDto pomodoroData);

}

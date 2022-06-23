package com.igorgorbunov3333.timer.service.pomodoro.provider.remote;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroMetadataDto;

public interface RemotePomodoroDataService {

    PomodoroMetadataDto provideRemoteData();

    void updateRemoteData(PomodoroMetadataDto pomodoroData);

}

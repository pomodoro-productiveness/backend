package com.igorgorbunov3333.timer.service.googledrive;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;

public interface GoogleDriveService {

    PomodoroDataDto getPomodoroData();
    void updatePomodoroData(PomodoroDataDto pomodoroData);

}

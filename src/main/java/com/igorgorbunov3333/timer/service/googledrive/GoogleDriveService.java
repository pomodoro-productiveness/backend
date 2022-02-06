package com.igorgorbunov3333.timer.service.googledrive;

import com.igorgorbunov3333.timer.model.dto.PomodoroDataDto;
import com.igorgorbunov3333.timer.model.dto.PomodoroDataDtoV2;

public interface GoogleDriveService {

    PomodoroDataDto getPomodoroData();

    void updatePomodoroData(PomodoroDataDtoV2 pomodoroData);

}

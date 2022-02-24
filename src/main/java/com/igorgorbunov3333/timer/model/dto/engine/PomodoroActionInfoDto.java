package com.igorgorbunov3333.timer.model.dto.engine;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroActionInfoDto {

    private final boolean doneSuccessfully;
    private final String failureMessage;
    private final PomodoroDto value;

}

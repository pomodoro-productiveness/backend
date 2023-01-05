package com.igorgorbunov3333.timer.console.rest.dto.pomodoro;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroAutoSaveRequestDto {

    private final int numbersToSaveAutomatically;
    private final Long tagGroupId;

}
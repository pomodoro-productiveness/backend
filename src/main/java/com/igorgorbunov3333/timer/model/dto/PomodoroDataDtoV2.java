package com.igorgorbunov3333.timer.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroDataDtoV2 {

    private List<PomodoroDtoV2> pomodoros;

    public static PomodoroDataDtoV2 createEmpty() {
        return new PomodoroDataDtoV2(List.of());
    }

}

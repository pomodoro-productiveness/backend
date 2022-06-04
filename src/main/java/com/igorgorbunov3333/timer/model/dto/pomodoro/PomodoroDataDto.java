package com.igorgorbunov3333.timer.model.dto.pomodoro;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroDataDto {

    private List<PomodoroDto> pomodoros;
    private List<PomodoroTagDto> pomodoroTags;

    public static PomodoroDataDto createEmpty() {
        return new PomodoroDataDto(List.of(), List.of());
    }

}

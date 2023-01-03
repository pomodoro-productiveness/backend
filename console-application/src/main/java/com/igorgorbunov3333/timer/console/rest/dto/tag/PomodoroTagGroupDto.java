package com.igorgorbunov3333.timer.console.rest.dto.tag;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PomodoroTagGroupDto {

    private Long id;
    private Set<PomodoroTagDto> pomodoroTags;
    private Long orderNumber;

}
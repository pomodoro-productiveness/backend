package com.igorgorbunov3333.timer.backend.model.dto.tag;

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

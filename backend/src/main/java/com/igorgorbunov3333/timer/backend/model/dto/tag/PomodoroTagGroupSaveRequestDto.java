package com.igorgorbunov3333.timer.backend.model.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@AllArgsConstructor
public class PomodoroTagGroupSaveRequestDto {

    @NotNull
    private final Set<String> tags;

}

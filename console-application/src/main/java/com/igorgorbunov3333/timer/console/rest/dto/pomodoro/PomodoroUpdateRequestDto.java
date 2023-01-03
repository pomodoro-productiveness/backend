package com.igorgorbunov3333.timer.console.rest.dto.pomodoro;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroUpdateRequestDto {

    @NotEmpty
    private List<Long> ids;
    @NotBlank
    private Set<String> tags;

}
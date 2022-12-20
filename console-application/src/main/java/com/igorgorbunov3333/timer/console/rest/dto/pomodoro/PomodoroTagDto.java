package com.igorgorbunov3333.timer.console.rest.dto.pomodoro;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PomodoroTagDto {

    private Long id;
    private String name;
    private boolean removed;

}
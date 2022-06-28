package com.igorgorbunov3333.timer.service.console.command.line.session;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class PomodoroTagInfo {

    private final int tagNumber;
    private final String tagName;

}

package com.igorgorbunov3333.timer.service.console.command.line.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroTagInfo {

    private int tagNumber;
    private String tagName;
    private boolean childTag;

}
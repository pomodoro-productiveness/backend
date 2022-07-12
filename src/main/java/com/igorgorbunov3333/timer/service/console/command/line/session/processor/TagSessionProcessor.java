package com.igorgorbunov3333.timer.service.console.command.line.session.processor;

import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;

import java.util.List;

public interface TagSessionProcessor {

    void process(List<PomodoroTagInfo> tagPositionToTags);

    //TODO: use enum to store actions
    String action();

}

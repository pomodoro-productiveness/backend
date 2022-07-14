package com.igorgorbunov3333.timer.service.console.command.line.session.processor.tag;

import com.igorgorbunov3333.timer.service.console.command.line.session.PomodoroTagInfo;
import com.igorgorbunov3333.timer.service.console.command.line.session.processor.BaseSessionProcessor;

import java.util.List;

public interface TagSessionProcessor extends BaseSessionProcessor {

    void process(List<PomodoroTagInfo> tagPositionToTags);

}

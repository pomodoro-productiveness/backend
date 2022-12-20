package com.igorgorbunov3333.timer.console.service.command.line.session.processor.tag;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroTagDto;
import com.igorgorbunov3333.timer.console.service.command.line.session.processor.BaseSessionProcessor;

import java.util.Map;

public interface TagSessionProcessor extends BaseSessionProcessor {

    void process(Map<Integer, PomodoroTagDto> tagPositionToTags);

}

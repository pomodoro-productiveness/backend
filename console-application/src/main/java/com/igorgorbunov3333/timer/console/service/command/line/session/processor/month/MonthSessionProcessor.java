package com.igorgorbunov3333.timer.console.service.command.line.session.processor.month;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.service.console.command.line.session.processor.BaseSessionProcessor;

import java.util.List;

public interface MonthSessionProcessor extends BaseSessionProcessor {

    void process(List<PomodoroDto> pomodoro);

}

package com.igorgorbunov3333.timer.backend.controller.report;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class StandardReportController {

    private final PomodoroStandardReporter pomodoroStandardReporter;

    public PomodoroStandardReportDto getStandardReport() {
        return null;
    }

}

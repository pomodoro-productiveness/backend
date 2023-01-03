package com.igorgorbunov3333.timer.backend.controller.report;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.PomodoroStandardReportrComponent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON_REPORTS + "/pomodoro/standard")
public class StandardReportController {

    private final PomodoroStandardReportrComponent pomodoroStandardReportrComponent;

    @GetMapping
    public PomodoroStandardReportDto getStandardReport(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return pomodoroStandardReportrComponent.report(from, to);
    }

}

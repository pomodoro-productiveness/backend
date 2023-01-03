package com.igorgorbunov3333.timer.console.service.pomodoro.report;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report.PomodoroStandardReportDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@AllArgsConstructor
public class PomodoroStandardReportComponent {

    private final BackendRestClient restClient;

    public PomodoroStandardReportDto getReport(LocalDate from, LocalDate to) {
        return restClient.get(
                BackendRestUtils.REST_PATH_REPORT_POMODORO_STANDARD,
                PomodoroStandardReportDto.class,
                Map.of("from", from.toString(), "to", to.toString())
        );
    }

}

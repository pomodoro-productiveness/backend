package com.igorgorbunov3333.timer.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.WorkTimeStandardReportDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class WorkTimeStandardReportCalculator implements WorkStandardAmountCalculable {

    @Getter
    private final PomodoroProperties pomodoroProperties;

    public WorkTimeStandardReportDto calculate(PeriodDto period, int pomodoroAmount, List<LocalDate> dayOffs) {
        int standardAmount = calculateWorkStandardAmount(period, dayOffs);

        int balanceAmount = pomodoroAmount - standardAmount;

        return new WorkTimeStandardReportDto(standardAmount, balanceAmount);
    }

}

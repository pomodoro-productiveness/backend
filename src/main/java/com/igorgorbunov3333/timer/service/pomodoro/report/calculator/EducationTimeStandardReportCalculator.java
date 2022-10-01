package com.igorgorbunov3333.timer.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.EducationTimeStandardReportDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EducationTimeStandardReportCalculator implements EducationStandardAmountCalculable {

    @Getter
    private final PomodoroProperties pomodoroProperties;

    public EducationTimeStandardReportDto calculate(PeriodDto period, int pomodoroAmount) {
        int standardAmount = calculateEducationStandardAmount(period);
        int balanceAmount = pomodoroAmount - standardAmount;
        double ratio = (double) pomodoroAmount / standardAmount;

        return new EducationTimeStandardReportDto(standardAmount, balanceAmount, pomodoroAmount, ratio);
    }

}

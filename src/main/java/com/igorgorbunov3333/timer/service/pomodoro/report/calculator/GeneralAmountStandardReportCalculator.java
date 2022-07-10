package com.igorgorbunov3333.timer.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.GeneralAmountStandardReportDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class GeneralAmountStandardReportCalculator implements WorkStandardAmountCalculable, EducationStandardAmountCalculable {

    @Getter
    private final PomodoroProperties pomodoroProperties;

    public GeneralAmountStandardReportDto calculate(PeriodDto period,
                                                    int allPomodoroAmount,
                                                    List<LocalDate> dayOffs) {
        int workStandardAmount = calculateWorkStandardAmount(period, dayOffs);
        int educationStandardAmount = calculateEducationStandardAmount(period);
        int generalStandardAmount = workStandardAmount + educationStandardAmount;

        int balance = allPomodoroAmount - generalStandardAmount;

        return new GeneralAmountStandardReportDto(generalStandardAmount, balance);
    }

}

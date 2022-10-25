package com.igorgorbunov3333.timer.backend.service.pomodoro.report;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report.EducationTimeStandardReportDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report.GeneralAmountStandardReportDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report.WorkTimeStandardReportDto;
import com.igorgorbunov3333.timer.backend.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.backend.repository.DayOffRepository;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.calculator.EducationPomodoroFilter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.calculator.EducationTimeStandardReportCalculator;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.calculator.GeneralAmountStandardReportCalculator;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.calculator.WorkPomodoroFilter;
import com.igorgorbunov3333.timer.backend.service.pomodoro.report.calculator.WorkTimeStandardReportCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroStandardReporter {

    private final WorkTimeStandardReportCalculator workTimeStandardReportCalculator;
    private final EducationTimeStandardReportCalculator educationTimeStandardReportCalculator;
    private final GeneralAmountStandardReportCalculator generalAmountStandardReportCalculator;
    private final WorkPomodoroFilter workPomodoroFilter;
    private final EducationPomodoroFilter educationPomodoroFilter;
    private final DayOffRepository dayOffRepository;

    public PomodoroStandardReportDto report(PeriodDto period, List<PomodoroDto> pomodoro) {
        LocalDate startDate = period.getStart().toLocalDate();

        List<LocalDate> dayOffs = dayOffRepository.findByDayGreaterThanEqualOrderByDay(startDate).stream()
                .map(DayOff::getDay)
                .collect(Collectors.toList());

        List<PomodoroDto> workPomodoro = workPomodoroFilter.filter(pomodoro);
        WorkTimeStandardReportDto workStandardReportDto = workTimeStandardReportCalculator.calculate(period, workPomodoro.size(), dayOffs);

        List<PomodoroDto> educationPomodoro = educationPomodoroFilter.filter(pomodoro);
        EducationTimeStandardReportDto educationTimeStandardReportDto =
                educationTimeStandardReportCalculator.calculate(period, educationPomodoro.size());

        GeneralAmountStandardReportDto generalAmountStandardReportDto =
                generalAmountStandardReportCalculator.calculate(period, pomodoro.size(), dayOffs);

        return new PomodoroStandardReportDto(generalAmountStandardReportDto, educationTimeStandardReportDto, workStandardReportDto);
    }

}

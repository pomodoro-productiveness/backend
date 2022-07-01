package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.BaseTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface EducationTimeStandardCalculator extends BaseTimeStandardCalculator {

    default int calculate(PomodoroPeriod period, LocalDate startDate) { //TODO: move common code to BaseTimeStandardCalculator
        List<PomodoroDto> pomodoroDtos = getPomodoroProviderCoordinator()
                .provide(
                        period,
                        getPomodoroProperties().getTag()
                                .getEducation()
                                .getName()
                );

        int actualAmount = pomodoroDtos.size();

        LocalDate today = getCurrentTimeService().getCurrentDateTime().toLocalDate();

        List<LocalDate> days = new ArrayList<>();
        for (LocalDate current = startDate;
             current.isBefore(today.plusDays(1L));
             current = current.plusDays(1L)) {
            if (!current.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                    && !current.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                days.add(current);
            }
        }

        int standardAmount = getPomodoroProperties().getStandard().getEducation() * days.size();

        return actualAmount - standardAmount;
    }

}

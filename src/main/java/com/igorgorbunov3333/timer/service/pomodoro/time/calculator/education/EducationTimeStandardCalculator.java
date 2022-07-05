package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.BaseTimeStandardCalculator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface EducationTimeStandardCalculator extends BaseTimeStandardCalculator {

    default int calculate(LocalDate startDate, List<PomodoroDto> pomodoro) { //TODO: move common code to BaseTimeStandardCalculator
        String educationTag = getPomodoroWorkTag();

        int actualAmount = (int) pomodoro.stream()
                .filter(filterByTag(educationTag))
                .count();

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

    private String getPomodoroWorkTag() {
        return getPomodoroProperties().getTag()
                .getEducation()
                .getName();
    }

    private Predicate<PomodoroDto> filterByTag(String workTag) {
        return p -> p.getTags().stream()
                .map(PomodoroTagDto::getName)
                .collect(Collectors.toList())
                .contains(workTag);
    }

}

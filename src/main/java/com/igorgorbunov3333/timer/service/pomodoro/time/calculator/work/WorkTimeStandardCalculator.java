package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work;

import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.BaseTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface WorkTimeStandardCalculator extends BaseTimeStandardCalculator {

    DayOffRepository getDayOffRepository();

    default int calculate(PomodoroPeriod period, LocalDate startDate) { //TODO: move common code to BaseTimeStandardCalculator
        List<LocalDate> dayOffs = getDayOffRepository().findByDayGreaterThanEqualOrderByDay(startDate).stream()
                .map(DayOff::getDay)
                .collect(Collectors.toList());

        String workTag = getPomodoroWorkTag();
        int actualAmount = getLocalPomodoroProviderCoordinator().provide(period, workTag)
                .size();

        LocalDate today = getCurrentTimeService().getCurrentDateTime().toLocalDate();

        List<LocalDate> days = new ArrayList<>();
        for (LocalDate current = startDate;
             current.isBefore(today.plusDays(1L));
             current = current.plusDays(1L)) {
            if (!current.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                    && !current.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                    && !dayOffs.contains(current)) {
                days.add(current);
            }
        }

        int daysAmount = CollectionUtils.isEmpty(days) ? 0 : days.size();
        int standardAmount = getPomodoroProperties().getStandard().getWork() * daysAmount;

        return actualAmount - standardAmount;
    }

    private String getPomodoroWorkTag() {
        return getPomodoroProperties().getTag()
                .getWork()
                .getName();
    }

}

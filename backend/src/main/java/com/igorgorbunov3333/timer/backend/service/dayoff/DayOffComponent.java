package com.igorgorbunov3333.timer.backend.service.dayoff;

import com.igorgorbunov3333.timer.backend.model.dto.dayoff.DayOffDto;
import com.igorgorbunov3333.timer.backend.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.backend.repository.DayOffRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.DayOffMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class DayOffComponent {

    private final DayOffRepository dayOffRepository;
    private final DayOffMapper dayOffMapper;

    public void saveAll(@NonNull List<DayOffDto> dayOffs) {
        List<DayOff> dayOffsEntities = dayOffMapper.toEntities(dayOffs);
        dayOffRepository.saveAll(dayOffsEntities);
    }

    public List<DayOffDto> getDayOffs(LocalDate from, LocalDate to) {
        List<DayOff> dayOffs;

        if (from == null || to == null) {
            dayOffs = dayOffRepository.findAll();
        } else {
            dayOffs = dayOffRepository.findByDayGreaterThanEqualAndDayLessThanEqualOrderByDay(from, to);
        }

        return dayOffMapper.toDtos(dayOffs);
    }

}

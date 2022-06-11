package com.igorgorbunov3333.timer.service.dayoff;

import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
public class DayOffSynchronizer {

    private final RemoteDayOffProvider remoteDayOffProvider;
    private final DayOffRepository dayOffRepository;

    @Transactional
    public void synchronize() {
        dayOffRepository.deleteAll();
        dayOffRepository.flush();

        List<DayOff> dayOffList = remoteDayOffProvider.provide();
        dayOffRepository.saveAll(dayOffList);
    }

}

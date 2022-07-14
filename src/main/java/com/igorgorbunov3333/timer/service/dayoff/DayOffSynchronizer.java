package com.igorgorbunov3333.timer.service.dayoff;

import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DayOffSynchronizer {

    private final RemoteDayOffProvider remoteDayOffProvider;
    private final DayOffRepository dayOffRepository;

    @Transactional
    public void synchronize() {
        log.debug("Started day off synchronization");

        dayOffRepository.deleteAll();
        dayOffRepository.flush();

        List<DayOff> dayOffs = remoteDayOffProvider.provide();
        dayOffRepository.saveAll(dayOffs);

        log.debug("Day off synchronization finished successfully");
    }

}

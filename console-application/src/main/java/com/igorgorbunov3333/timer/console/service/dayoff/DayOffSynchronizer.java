package com.igorgorbunov3333.timer.console.service.dayoff;

import com.igorgorbunov3333.timer.console.rest.dto.DayOffDto;
import com.igorgorbunov3333.timer.console.rest.dto.dayoff.DayOffsSaveRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DayOffSynchronizer {

    private final RemoteDayOffProvider remoteDayOffProvider;
    private final RestTemplate restTemplate;

    public void synchronize() {
        log.debug("Started day off synchronization");

        List<DayOffDto> dayOffs = remoteDayOffProvider.provide();

        DayOffsSaveRequestDto dayOffsSaveRequestDto = new DayOffsSaveRequestDto(dayOffs);



        log.debug("Day off synchronization finished successfully");
    }

}

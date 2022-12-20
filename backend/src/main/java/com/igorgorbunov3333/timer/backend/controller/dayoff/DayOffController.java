package com.igorgorbunov3333.timer.backend.controller.dayoff;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.dayoff.DayOffDto;
import com.igorgorbunov3333.timer.backend.model.dto.dayoff.DayOffsSaveRequestDto;
import com.igorgorbunov3333.timer.backend.service.dayoff.DayOffComponent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON + "/day-offs")
public class DayOffController {

    private final DayOffComponent dayOffComponent;

    @PostMapping
    public void save(@RequestBody DayOffsSaveRequestDto request) {
        List<DayOffDto> dayOffs = request.getDayOffs();

        log.info(String.format("Day offs in amount of [%d] items received for save", dayOffs.size()));

        dayOffComponent.saveAll(request.getDayOffs());

        log.info(String.format("Day offs saved in amount of [%d] items successfully", dayOffs.size()));
    }

}

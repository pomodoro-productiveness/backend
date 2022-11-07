package com.igorgorbunov3333.timer.backend.controller;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroAutoSaveRequestDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroSaveRequestDto;
import com.igorgorbunov3333.timer.backend.service.pomodoro.saver.PomodoroAutoSaver;
import com.igorgorbunov3333.timer.backend.service.pomodoro.saver.PomodoroSaver;
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
@RequestMapping(RestPathUtil.COMMON)
public class PomodoroController {

    private PomodoroAutoSaver pomodoroAutoSaver;
    private PomodoroSaver pomodoroSaver;

    @PostMapping(path = "/pomodoro")
    public PomodoroDto save(@RequestBody PomodoroSaveRequestDto saveRequestDto) {
        PomodoroDto savedSinglePomodoro = pomodoroSaver.save(saveRequestDto);

        log.info("Pomodoro with id [{}] saved successfully", savedSinglePomodoro.getId());

        return savedSinglePomodoro;
    }

    @PostMapping(path = "/pomodoro/auto")
    public List<PomodoroDto> save(@RequestBody PomodoroAutoSaveRequestDto autoSaveRequestDto) {
        List<PomodoroDto> savedPomodoro = pomodoroAutoSaver.save(
                autoSaveRequestDto.getNumbersToSaveAutomatically(),
                autoSaveRequestDto.getTagGroupId()
        );

        savedPomodoro.forEach(pomodoro -> log.info("Pomodoro with id [{}] saved automatically", pomodoro.getId()));

        return savedPomodoro;
    }

}

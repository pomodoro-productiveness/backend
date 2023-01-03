package com.igorgorbunov3333.timer.backend.controller.pomodoro;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroAutoSaveRequestDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroSaveRequestDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroUpdateRequestDto;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.PomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.pomodoro.remover.PomodoroRemover;
import com.igorgorbunov3333.timer.backend.service.pomodoro.saver.PomodoroAutoSaver;
import com.igorgorbunov3333.timer.backend.service.pomodoro.saver.PomodoroSaver;
import com.igorgorbunov3333.timer.backend.service.pomodoro.updater.PomodoroUpdater;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON + "/pomodoro")
public class PomodoroController {

    private PomodoroAutoSaver pomodoroAutoSaver;
    private PomodoroSaver pomodoroSaver;
    private PomodoroProvider pomodoroProvider;
    private PomodoroRemover pomodoroRemover;
    private PomodoroUpdater pomodoroUpdater;

    @PostMapping
    public PomodoroDto save(@RequestBody PomodoroSaveRequestDto saveRequestDto) {
        PomodoroDto savedSinglePomodoro = pomodoroSaver.save(saveRequestDto);

        log.info("Pomodoro with id [{}] saved successfully", savedSinglePomodoro.getId());

        return savedSinglePomodoro;
    }

    @PostMapping(path = "/auto")
    public List<PomodoroDto> save(@RequestBody PomodoroAutoSaveRequestDto autoSaveRequestDto) {
        List<PomodoroDto> savedPomodoro = pomodoroAutoSaver.save(
                autoSaveRequestDto.getNumbersToSaveAutomatically(),
                autoSaveRequestDto.getTagGroupId()
        );

        savedPomodoro.forEach(pomodoro -> log.info("Pomodoro with id [{}] saved automatically", pomodoro.getId()));

        return savedPomodoro;
    }

    @GetMapping
    public List<PomodoroDto> getPomodoro(@RequestParam LocalDate start,
                                         @RequestParam LocalDate end,
                                         @RequestParam(required = false) String tag) {
        return pomodoroProvider.provide(
                start.atStartOfDay().atOffset(ZoneOffset.UTC).toZonedDateTime(),
                end.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toZonedDateTime(),
                tag
        );
    }
    
    @DeleteMapping
    public void deletePomodoro(@RequestParam long pomodoroId) {
        pomodoroRemover.remove(pomodoroId);
    }

    @PutMapping
    public void updatePomodoroWithTag(@RequestBody PomodoroUpdateRequestDto request) {
        pomodoroUpdater.updatePomodoroWithTag(request.getIds(), request.getTags());
    }

}

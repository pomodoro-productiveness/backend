package com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl;

import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.BasePomodoroProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultPomodoroProvider implements BasePomodoroProvider {

    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;

}

package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagBunch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PomodoroTagBunchRepository extends JpaRepository<PomodoroTagBunch, Long> {

    List<PomodoroTagBunch> findTop10ByOrderByIdDesc();

}

package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTagGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PomodoroTagGroupRepository extends JpaRepository<PomodoroTagGroup, Long> {

    @EntityGraph(attributePaths = "pomodoroTags")
    List<PomodoroTagGroup> findTop30ByOrderByOrderNumberDesc();

}

package com.igorgorbunov3333.timer.backend.repository;

import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

    @EntityGraph(attributePaths = {"pomodoroTagGroup.pomodoroTags"})
    List<Pomodoro> findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(ZonedDateTime startRange, ZonedDateTime endRange);

    boolean existsByPomodoroTagGroupPomodoroTagsName(String tagName);

    @EntityGraph(attributePaths = {"pomodoroTagGroup.pomodoroTags"})
    List<Pomodoro> findByIdIn(List<Long> ids);

}

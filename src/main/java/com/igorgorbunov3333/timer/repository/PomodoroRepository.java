package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

    @EntityGraph(attributePaths = {"tags"})
    List<Pomodoro> findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(ZonedDateTime startRange, ZonedDateTime endRange);

    boolean existsByTagsName(String tagName);

    List<Pomodoro> findByTagsNameIn(Set<String> tags);

}

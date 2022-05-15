package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

    long countByStartTimeAfterAndEndTimeBefore(ZonedDateTime startRange, ZonedDateTime endRange);

    List<Pomodoro> findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(ZonedDateTime startRange, ZonedDateTime endRange);

    Optional<Pomodoro> findTopByOrderByEndTimeDesc();

    List<Pomodoro> findByEndTimeLessThanEqual(ZonedDateTime timestamp);

}

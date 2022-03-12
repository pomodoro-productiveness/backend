package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

    long countByStartTimeAfterAndEndTimeBefore(LocalDateTime startRange, LocalDateTime endRange);

    List<Pomodoro> findByStartTimeAfterAndEndTimeBefore(LocalDateTime startRange, LocalDateTime endRange);

    Optional<Pomodoro> findTopByOrderByEndTimeDesc();

    List<Pomodoro> findByEndTimeLessThanEqual(LocalDateTime timestamp);

}

package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

    List<Pomodoro> findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(ZonedDateTime startRange, ZonedDateTime endRange);

    List<Pomodoro> findByStartTimeAfterAndEndTimeBeforeAndTagNameOrderByStartTime(ZonedDateTime startRange,
                                                                                  ZonedDateTime endRange,
                                                                                  String tag);

    boolean existsByTagName(String tagName);

}

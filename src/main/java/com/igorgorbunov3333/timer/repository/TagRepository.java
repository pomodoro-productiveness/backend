package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<PomodoroTag, Long> {

    List<PomodoroTag> findByParentIsNull();

    Optional<PomodoroTag> findByName(String parentTagName);

    boolean existsByName(String tagName);

}

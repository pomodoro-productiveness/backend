package com.igorgorbunov3333.timer.backend.repository;

import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PomodoroTagRepository extends JpaRepository<PomodoroTag, Long> {

    List<PomodoroTag> findByNameIn(Set<String> tagNames);

    Optional<PomodoroTag> findByName(String name);

    boolean existsByName(String tagName);

    void deleteByName(String tagName);

}

package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.PomodoroTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<PomodoroTag, Long> {
}

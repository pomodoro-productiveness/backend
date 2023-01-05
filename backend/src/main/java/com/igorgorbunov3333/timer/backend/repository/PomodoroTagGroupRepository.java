package com.igorgorbunov3333.timer.backend.repository;

import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PomodoroTagGroupRepository extends JpaRepository<PomodoroTagGroup, Long> {

    @EntityGraph(attributePaths = "pomodoroTags")
    List<PomodoroTagGroup> findByOrderByOrderNumberDesc();

    default PomodoroTagGroup getPomodoroTagGroup(long tagGroupId) {
        return findById(tagGroupId)
                .orElseThrow(() -> new EntityDoesNotExist(
                        String.format("[%s] with id [%d] does not exist", this.getClass().getSimpleName(), tagGroupId))
                );
    }

}

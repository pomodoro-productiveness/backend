package com.igorgorbunov3333.timer.backend.repository;

import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import com.igorgorbunov3333.timer.backend.service.exception.EntityDoesNotExist;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PomodoroTagRepository extends JpaRepository<PomodoroTag, Long> {

    List<PomodoroTag> findByNameIn(Set<String> tagNames);

    boolean existsByName(String tagName);

    void deleteByName(String tagName);

    Optional<PomodoroTag> findByName(String tagName);

    default PomodoroTag getByName(@NonNull String tagName) {
        return findByName(tagName).orElseThrow(
                () -> new EntityDoesNotExist(String.format("Tag with name [%s] does not exist", tagName))
        );
    }

}

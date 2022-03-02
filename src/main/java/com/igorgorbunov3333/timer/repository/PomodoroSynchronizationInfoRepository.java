package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PomodoroSynchronizationInfoRepository extends JpaRepository<PomodoroSynchronizationInfo, Long> {

    List<PomodoroSynchronizationInfo> findByTimeBefore(LocalDateTime time);

    Optional<PomodoroSynchronizationInfo> findTopByOrderByTimeDesc();

}

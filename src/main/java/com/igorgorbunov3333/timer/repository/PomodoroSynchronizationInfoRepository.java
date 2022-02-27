package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PomodoroSynchronizationInfoRepository extends JpaRepository<PomodoroSynchronizationInfo, Long> {
}

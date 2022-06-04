package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.SynchronizationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SynchronizationInfoRepository extends JpaRepository<SynchronizationInfo, Long> {

    Optional<SynchronizationInfo> findTopByOrderByTimeDesc();

}

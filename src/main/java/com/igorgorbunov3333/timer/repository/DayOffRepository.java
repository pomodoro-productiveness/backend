package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {
}

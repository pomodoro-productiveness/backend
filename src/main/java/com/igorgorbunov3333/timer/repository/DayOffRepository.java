package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    List<DayOff> findByDayGreaterThanEqualOrderByDay(LocalDate day);

}

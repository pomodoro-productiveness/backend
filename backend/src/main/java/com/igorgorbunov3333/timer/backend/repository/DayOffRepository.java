package com.igorgorbunov3333.timer.backend.repository;

import com.igorgorbunov3333.timer.backend.model.entity.dayoff.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    List<DayOff> findByDayGreaterThanEqualOrderByDay(LocalDate day);

    List<DayOff> findByDayGreaterThanEqualAndDayLessThanEqualOrderByDay(LocalDate start, LocalDate end);

    Optional<DayOff> findByDay(LocalDate day);

}

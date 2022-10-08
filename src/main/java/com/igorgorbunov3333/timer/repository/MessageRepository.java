package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface MessageRepository extends JpaRepository<Message, Long> {

    boolean existsByDate(LocalDate date);

}

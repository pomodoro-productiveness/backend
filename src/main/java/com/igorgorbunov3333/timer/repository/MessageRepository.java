package com.igorgorbunov3333.timer.repository;

import com.igorgorbunov3333.timer.model.entity.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByDate(LocalDate date);

}

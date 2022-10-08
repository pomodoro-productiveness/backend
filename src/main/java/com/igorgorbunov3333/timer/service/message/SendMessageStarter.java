package com.igorgorbunov3333.timer.service.message;

import com.igorgorbunov3333.timer.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.repository.MessageRepository;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
@Component
@AllArgsConstructor
public class SendMessageStarter {

    private final CurrentTimeService currentTimeService;
    private final MessageRepository messageRepository;
    private final MessageSenderCoordinator messageSenderCoordinator;

    public void start() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();
        LocalDate reportDate = today.minusDays(1L);

        boolean messagePresentForDate = messageRepository.existsByDate(reportDate);
        if (messagePresentForDate) {
            log.debug("Message for [{}] is already present, skip sending...", reportDate);
            return;
        }

        if (isStartOfWeek(today)) {
            messageSenderCoordinator.coordinate(MessagePeriod.WEEK, reportDate);
        } else {
            messageSenderCoordinator.coordinate(MessagePeriod.DAY, reportDate);
        }

    }

    private boolean isStartOfWeek(LocalDate today) {
        return DayOfWeek.MONDAY.equals(today.getDayOfWeek());
    }

}

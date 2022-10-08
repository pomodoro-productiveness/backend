package com.igorgorbunov3333.timer.service.message;

import com.igorgorbunov3333.timer.model.entity.enums.MessagePeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessageSenderCoordinator {

    private final Map<MessagePeriod, MessageSender> periodsBySenders;

    public MessageSenderCoordinator(List<MessageSender> messageSenders) {
        periodsBySenders = messageSenders.stream()
                .collect(Collectors.toMap(MessageSender::getMessagePeriod, Function.identity()));
    }

    public void coordinate(MessagePeriod period, LocalDate reportDate) {
        MessageSender messageSender = periodsBySenders.get(period);

        if (messageSender == null) {
            log.warn("No MessageSender with period {}", period);
            return;
        }

        log.info("DailyMessageSender started");

        messageSender.send(reportDate);

        log.info("Daily message for {} sent", period);
    }

}

package com.igorgorbunov3333.timer.backend.service.message;

import com.igorgorbunov3333.timer.backend.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.backend.model.entity.message.Message;
import com.igorgorbunov3333.timer.backend.repository.MessageRepository;
import com.igorgorbunov3333.timer.backend.service.message.pomodoro.report.impl.DailyPomodoroStandardReportMessageProvider;
import com.igorgorbunov3333.timer.backend.service.message.pomodoro.report.impl.WeekPomodoroStandardReportMessageProvider;
import com.igorgorbunov3333.timer.backend.service.message.telegram.TelegramHttpApiCaller;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class PomodoroStandardReportMessageSender {

    private final DailyPomodoroStandardReportMessageProvider dailyPomodoroStandardReportMessageProvider;
    private final WeekPomodoroStandardReportMessageProvider weekPomodoroStandardReportMessageProvider;
    private final MessageRepository messageRepository;
    private final TelegramHttpApiCaller telegramHttpApiCaller;
    private final CurrentTimeService currentTimeService;

    @Transactional
    public void send() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();
        LocalDate reportDate = today.minusDays(1L);
        boolean messagePresentForDate = messageRepository.existsByDate(reportDate);
        if (messagePresentForDate) {
            log.debug("Message for [{}] is already present, skip sending...", reportDate);
            return;
        }

        String dailyReportMessage = dailyPomodoroStandardReportMessageProvider.provide(reportDate);
        String weeklyReportMessage = weekPomodoroStandardReportMessageProvider.provide(reportDate);

        String finalMessage = String.join("\n\n", dailyReportMessage, weeklyReportMessage);

        telegramHttpApiCaller.send(finalMessage);

        Message message = new Message(null, reportDate, MessagePeriod.DAY);

        messageRepository.save(message);

        deleteOldMessages();
    }

    private void deleteOldMessages() {
        List<Message> messagesToRemove = messageRepository.findAll();
        messagesToRemove.sort(Comparator.comparing(Message::getDate));
        messagesToRemove.remove(messagesToRemove.size() - 1);
        messageRepository.deleteAll(messagesToRemove);
    }

}

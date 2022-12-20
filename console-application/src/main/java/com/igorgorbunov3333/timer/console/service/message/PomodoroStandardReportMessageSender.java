package com.igorgorbunov3333.timer.console.service.message;

import com.igorgorbunov3333.timer.console.rest.dto.message.MessageDto;
import com.igorgorbunov3333.timer.console.service.message.pomodoro.report.impl.DailyPomodoroStandardReportMessageProvider;
import com.igorgorbunov3333.timer.console.service.message.pomodoro.report.impl.WeekPomodoroStandardReportMessageProvider;
import com.igorgorbunov3333.timer.console.service.message.telegram.TelegramHttpApiCaller;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@AllArgsConstructor
public class PomodoroStandardReportMessageSender {

    private final DailyPomodoroStandardReportMessageProvider dailyPomodoroStandardReportMessageProvider;
    private final WeekPomodoroStandardReportMessageProvider weekPomodoroStandardReportMessageProvider;
    private final MessageComponent messageComponent;
    private final TelegramHttpApiCaller telegramHttpApiCaller;
    private final CurrentTimeService currentTimeService;

    public void send() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();
        LocalDate reportDate = today.minusDays(1L);
        boolean messagePresentForDate = messageComponent.existsByDate(reportDate);
        if (messagePresentForDate) {
            log.debug("Message for [{}] is already present, skip sending...", reportDate);
            return;
        }

        String dailyReportMessage = dailyPomodoroStandardReportMessageProvider.provide(reportDate);
        String weeklyReportMessage = weekPomodoroStandardReportMessageProvider.provide(reportDate);

        String finalMessage = String.join("\n\n", dailyReportMessage, weeklyReportMessage);

        telegramHttpApiCaller.send(finalMessage);


        MessageDto message = new MessageDto(reportDate, "day");

        messageComponent.save(message);
    }

}

package com.igorgorbunov3333.timer.service.message;

import com.igorgorbunov3333.timer.model.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.repository.MessageRepository;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class DailyMessageSender implements MessageSender {

    @Getter
    private final MessageRepository messageRepository;
    @Getter
    private final TelegramMessageSender telegramMessageSender;
    private final PomodoroStandardReporter pomodoroStandardReporter;
    private final DailyPomodoroProvider dailyPomodoroProvider;

    @Override
    public MessagePeriod getMessagePeriod() {
        return MessagePeriod.DAY;
    }

    @Override
    public void send(LocalDate reportDate) {
        DailyPomodoroDto dailyPomodoroDto = dailyPomodoroProvider.provide(reportDate);
        PomodoroStandardReportDto report = pomodoroStandardReporter.report(dailyPomodoroDto.calculatePeriod(), dailyPomodoroDto.getPomodoro());

        String header = "Report for " + reportDate + "\n";

        buildMessageAndSend(report, header, reportDate);
    }

}

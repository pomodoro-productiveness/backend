package com.igorgorbunov3333.timer.service.message;

import com.igorgorbunov3333.timer.model.dto.pomodoro.period.DailyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.model.entity.message.Message;
import com.igorgorbunov3333.timer.repository.MessageRepository;
import com.igorgorbunov3333.timer.service.pomodoro.provider.impl.DailyPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class DailyMessageSender {

    private final MessageRepository messageRepository;
    private final CurrentTimeService currentTimeService;
    private final PomodoroStandardReporter pomodoroStandardReporter;
    private final DailyPomodoroProvider dailyPomodoroProvider;
    private final TelegramMessageSender telegramMessageSender;

    public void send() {
        log.info("DailyMessageSender started");

        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        LocalDate reportDate = today.minusDays(1L);

        Optional<Message> messageOpt = messageRepository.findByDate(reportDate);

        if (messageOpt.isEmpty()) {
            log.info("Daily message for {} is absent, sending a message", reportDate);
            send(reportDate);

            return;
        }

        log.info("Daily message for {} is present", reportDate);
    }

    private void send(LocalDate reportDate) {
        DailyPomodoroDto dailyPomodoroDto = dailyPomodoroProvider.provide(reportDate);
        PomodoroStandardReportDto reportDto = pomodoroStandardReporter.report(dailyPomodoroDto.calculatePeriod(), dailyPomodoroDto.getPomodoro());

        String reportMessage = prepareReportMessage(reportDto, reportDate);

        telegramMessageSender.send(reportMessage);

        Message message = new Message(null, reportDate, MessagePeriod.DAY);
        messageRepository.save(message);
    }

    private String prepareReportMessage(PomodoroStandardReportDto reportDto, LocalDate reportDate) {
        String header = String.format("Report for %s \n", reportDate);
        String workStandardRow = "Work standard: "
                + reportDto.getWork().getActualAmount()
                + "/" + reportDto.getWork().getStandardAmount() + StringUtils.SPACE
                + ", in percents: " + (int) (reportDto.getWork().getRatio() * 100) + "%";
        String educationStandardRow = "Education standard: "
                + reportDto.getEducation().getActualAmount()
                + "/" + reportDto.getEducation().getStandardAmount() + StringUtils.SPACE
                + ", in percents: " + (int) (reportDto.getEducation().getRatio() * 100) + "%";
        String generalStandardRow = "General standard: "
                + reportDto.getAmount().getActualAmount()
                + "/" + reportDto.getEducation().getStandardAmount() + StringUtils.SPACE
                + ", in percents: " + (int) (reportDto.getAmount().getRatio() * 100) + "%";

        return String.join("\n", header, workStandardRow, educationStandardRow, generalStandardRow);
    }

}

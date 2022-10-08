package com.igorgorbunov3333.timer.service.message;

import com.igorgorbunov3333.timer.model.dto.pomodoro.report.AbstractStandardReportDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.model.entity.message.Message;
import com.igorgorbunov3333.timer.repository.MessageRepository;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public interface MessageSender {

    String IN_PERCENTS = ", in percents: ";
    String PERCENTAGE_SYMBOL = "%";

    void send(LocalDate reportDate);

    MessagePeriod getMessagePeriod();

    MessageRepository getMessageRepository();

    TelegramMessageSender getTelegramMessageSender();

    default void buildMessageAndSend(PomodoroStandardReportDto report, String messageHeader, LocalDate reportDate) {
        String reportMessage = buildReportMessage(report, messageHeader);

        Message messageToSave = send(reportDate, reportMessage);

        getMessageRepository().save(messageToSave);
    }

    private String buildReportMessage(PomodoroStandardReportDto reportDto, String header) {
        String workStandardRow = buildRow("Work standard: ", reportDto.getWork());
        String educationStandardRow = buildRow("Education standard: ", reportDto.getEducation());
        String generalStandardRow = buildRow("General standard: ", reportDto.getAmount());

        return String.join("\n", header, workStandardRow, educationStandardRow, generalStandardRow);
    }

    private String buildRow(String header, AbstractStandardReportDto standardReport) {
        return header + standardReport.getActualAmount()
                + "/" + standardReport.getStandardAmount() + StringUtils.SPACE
                + IN_PERCENTS + (int) (standardReport.getRatio() * 100) + PERCENTAGE_SYMBOL;
    }

    private Message send(LocalDate reportDate, String reportMessage) {
        getTelegramMessageSender().send(reportMessage);

        return new Message(null, reportDate, MessagePeriod.DAY);
    }

}

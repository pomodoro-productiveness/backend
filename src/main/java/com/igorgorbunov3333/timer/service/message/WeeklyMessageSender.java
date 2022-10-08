package com.igorgorbunov3333.timer.service.message;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.repository.MessageRepository;
import com.igorgorbunov3333.timer.service.exception.MessageProcessingException;
import com.igorgorbunov3333.timer.service.period.WeekPeriodHelper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class WeeklyMessageSender implements MessageSender {

    @Getter
    private final MessageRepository messageRepository;
    private final WeekPeriodHelper weekPeriodHelper;
    private final WeeklyPomodoroProvider weeklyPomodoroProvider;
    private final PomodoroStandardReporter pomodoroStandardReporter;
    @Getter
    private final TelegramMessageSender telegramMessageSender;

    @Override
    public MessagePeriod getMessagePeriod() {
        return MessagePeriod.WEEK;
    }

    @Override
    public void send(LocalDate reportDate) {
        PeriodDto previousWeekPeriod = weekPeriodHelper.providePreviousWeekPeriod();

        List<WeeklyPomodoroDto> weeklyPomodoroDtoList = weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(previousWeekPeriod);

        if (CollectionUtils.isEmpty(weeklyPomodoroDtoList)) {
            throw new MessageProcessingException("No WeeklyPomodoroDto for period " + previousWeekPeriod);
        }

        WeeklyPomodoroDto weeklyPomodoroDto = weeklyPomodoroDtoList.get(0);

        PomodoroStandardReportDto report = pomodoroStandardReporter.report(weeklyPomodoroDto.getPeriod(), weeklyPomodoroDto.getPomodoro());

        String header = "Report for " + weeklyPomodoroDto.getPeriod() + "\n";

        buildMessageAndSend(report, header, reportDate);
    }

}

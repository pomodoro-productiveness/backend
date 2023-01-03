package com.igorgorbunov3333.timer.console.service.message.pomodoro.report;

import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report.AbstractStandardReportDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report.PomodoroStandardReportDto;
import org.apache.commons.lang3.StringUtils;

public interface MessageProvider {

    default String buildReportMessage(PomodoroStandardReportDto reportDto, String header) {
        String workStandardRow = buildRow("Work standard: ", reportDto.getWork());
        String educationStandardRow = buildRow("Education standard: ", reportDto.getEducation());
        String generalStandardRow = buildRow("General standard: ", reportDto.getAmount());

        return String.join("\n", header, workStandardRow, educationStandardRow, generalStandardRow);
    }

    private String buildRow(String header, AbstractStandardReportDto standardReport) {
        return header + standardReport.getActualAmount()
                + "/" + standardReport.getStandardAmount() + StringUtils.SPACE
                + ", in percents: " + (int) (standardReport.getRatio() * 100) + "%";
    }

}

package com.igorgorbunov3333.timer.backend.controller.report;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.tag.report.TagDurationReportDto;
import com.igorgorbunov3333.timer.backend.service.tag.report.TagDurationReportsComposer;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON_REPORTS + "/tags/duration")
public class TagDurationReportController {

    private final TagDurationReportsComposer tagDurationReport;

    @GetMapping
    public TagDurationReportDto getTagDurationReport(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return tagDurationReport.getReport(from, to);
    }

}

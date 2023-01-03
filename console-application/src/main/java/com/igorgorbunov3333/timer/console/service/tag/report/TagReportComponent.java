package com.igorgorbunov3333.timer.console.service.tag.report;

import com.igorgorbunov3333.timer.console.rest.BackendRestUtils;
import com.igorgorbunov3333.timer.console.rest.client.BackendRestClient;
import com.igorgorbunov3333.timer.console.rest.dto.tag.report.TagDurationReportDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@AllArgsConstructor
public class TagReportComponent {

    private final BackendRestClient backendRestClient;

    public TagDurationReportDto getTagDurationReport(@NonNull LocalDate from, @NonNull LocalDate to) {
        return backendRestClient.get(
                BackendRestUtils.REST_PATH_REPORT_TAGS_DURATION,
                TagDurationReportDto.class,
                Map.of("from", from.toString(), "to", to.toString())
        );
    }

}

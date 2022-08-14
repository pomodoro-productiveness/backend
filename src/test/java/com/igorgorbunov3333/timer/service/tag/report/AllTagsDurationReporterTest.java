package com.igorgorbunov3333.timer.service.tag.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.service.tag.TagToTagsFromPomodoroMappingsBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllTagsDurationReporterTest {

    @InjectMocks
    private AllTagsDurationReporter testee;

    @Mock
    private TagToTagsFromPomodoroMappingsBuilder tagToTagsFromPomodoroMappingsBuilder;

    @Test
    void report_WhenPomodoroExists_ThenReturnReportRows() throws JsonProcessingException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("data/service/tag/report/AllTagsDurationReporter_weekly_pomodoro.json");
        String stringPomodoro = new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining(StringUtils.LF));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<PomodoroDto> pomodoro = objectMapper.readValue(stringPomodoro, new TypeReference<>() {
        });

        Map<String, Set<String>> tagMappings = Map.of("education", Set.of("book", "SpringSecurity", "algorithms",
                        "html/css", "educativePlatform", "EducativeCompany"),
                "algorithms", Set.of("education", "educativePlatform"),
                "SpringSecurity", Set.of("book", "education"),
                "html/css", Set.of("education", "EducativeCompany"),
                "book", Set.of("education", "SpringSecurity"),
                "pomodoro", Set.of(),
                "educativePlatform", Set.of("education", "algorithms"),
                "EducativeCompany", Set.of("education", "html/css")
        );
        when(tagToTagsFromPomodoroMappingsBuilder.buildTagMappings(pomodoro)).thenReturn(tagMappings);

        List<TagDurationReportDto> actual = testee.reportForEachTag(pomodoro);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(buildExpectedReports());
    }

    private List<TagDurationReportDto> buildExpectedReports() {
        List<TagDurationReportDto> reports = new ArrayList<>();

        reports.add(new TagDurationReportDto(
                buildReportRow("book", 15673),
                List.of(
                        buildReportRow("#education #SpringSecurity", 15673)
                )));

        reports.add(new TagDurationReportDto(
                buildReportRow("pomodoro", 28858),
                List.of()
        ));

        reports.add(new TagDurationReportDto(
                buildReportRow("educativePlatform", 2413),
                List.of(
                        buildReportRow("#algorithms #education", 2413)
                )));

        reports.add(new TagDurationReportDto(
                buildReportRow("SpringSecurity", 15673),
                List.of(
                        buildReportRow("#education #book", 15673)
                )));

        reports.add(new TagDurationReportDto(
                buildReportRow("html/css", 6807),
                List.of(
                        buildReportRow("#education #EducativeCompany", 6807)
                )));

        reports.add(new TagDurationReportDto(
                buildReportRow("algorithms", 2413),
                List.of(
                        buildReportRow("#education #educativePlatform", 2413)
                )));

        reports.add(new TagDurationReportDto(
                buildReportRow("EducativeCompany", 6807),
                List.of(
                        buildReportRow("#education #html/css", 6807)
                )));

        reports.add(new TagDurationReportDto(
                buildReportRow("education", 24893),
                List.of(
                        buildReportRow("#algorithms #educativePlatform", 2413),
                        buildReportRow("#EducativeCompany #html/css", 6807),
                        buildReportRow("#SpringSecurity #book", 15673)
                )));

        return reports;
    }

    private TagDurationReportRowDto buildReportRow(String tag, long duration) {
        return new TagDurationReportRowDto(tag, duration, null, null);
    }

}
